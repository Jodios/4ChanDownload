package main

import (
	_ "embed"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"io/fs"
	"log"
	"net/http"
	"os"
	"sync"
	"time"
)

const (
	url            = "https://a.4cdn.org"
	imagesUrl      = "https://i.4cdn.org"
	defaultBoard   = "g"
	maxConcurrency = 30
)

var (
	posts []Post
	lock  = &sync.Mutex{}
	board = defaultBoard
)

func main() {
	wg := &sync.WaitGroup{}

	if len(os.Args[1:]) == 1 {
		board = os.Args[1]
	}
	threadsUrl := url + "/" + board + "/threads.json"

	postChannel := make(chan string)
	threads := GetListOfThreads(threadsUrl)
	for i := 0; i < maxConcurrency; i++ {
		wg.Add(1)
		go GetPosts(postChannel, wg)
	}
	for _, u := range threads {
		postChannel <- u
	}
	close(postChannel)
	wg.Wait()

	imageChannel := make(chan SaveFileInfo)
	for i := 0; i < maxConcurrency; i++ {
		wg.Add(1)
		go DownloadImage(imageChannel, wg)
	}
	for _, post := range posts {
		imageChannel <- SaveFileInfo{
			Board:     board,
			Name:      *post.FileName,
			Extension: *post.Extension,
			ID:        *post.AttachementId,
			Url:       fmt.Sprintf("%v/%v/%v%v", imagesUrl, board, *post.AttachementId, *post.Extension),
		}
	}

	close(imageChannel)
	wg.Wait()
}

func Write(fn string, data []byte) {
	os.WriteFile(
		fn,
		data,
		os.ModePerm,
	)
}
func Get(u string) []byte {
	client := http.Client{
		Timeout: time.Second * 5,
	}
	resp, err := client.Get(u)
	if err != nil {
		fmt.Println(err)
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		fmt.Printf("%v: %v\n", resp.Status, u)
	}
	data, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Fatal(err)
	}
	return data
}
func GetListOfThreads(u string) []string {
	pages := make([]Page, 0)
	err := json.Unmarshal(Get(u), &pages)
	if err != nil {
		log.Fatal(err)
	}
	mainThreadsList := make([]string, 0)
	for _, page := range pages {
		for _, thread := range page.Threads {
			mainThreadsList = append(
				mainThreadsList,
				fmt.Sprintf("%s/%s/thread/%d.json", url, board, thread.Number),
			)
		}
	}
	return mainThreadsList
}
func GetPosts(ch chan string, wg *sync.WaitGroup) {
	defer wg.Done()
	for u := range ch {
		threads := make([]Thread, 0)
		thread := Thread{}
		json.Unmarshal(Get(u), &thread)
		threads = append(threads, thread)
		p := make([]Post, 0)
		for _, thread := range threads {
			for _, post := range thread.Posts {
				if post.AttachementId == nil || post.Extension == nil || post.FileName == nil {
					continue
				}
				p = append(p, post)
			}
		}
		AppendPost(p)
	}
}
func DownloadImage(ch chan SaveFileInfo, wg *sync.WaitGroup) {
	defer wg.Done()
	for saveInfo := range ch {
		filePath := saveInfo.Board + "/" + saveInfo.Name + saveInfo.Extension
		if _, err := os.Stat(saveInfo.Board); errors.Is(err, os.ErrNotExist) {
			if err := os.Mkdir(saveInfo.Board, fs.ModePerm); err != nil {
				fmt.Println(err)
			}
		}
		if _, err := os.Stat(filePath); err == nil {
			continue
		}
		Write(filePath, Get(saveInfo.Url))
	}
}
func AppendPost(p []Post) {
	lock.Lock()
	defer lock.Unlock()
	posts = append(posts, p...)
}

type Page struct {
	Threads []Thread `json:"threads,omitempty"`
}
type Thread struct {
	Number int    `json:"no,omitempty"`
	Posts  []Post `json:"posts,omitempty"`
}
type Post struct {
	FileName      *string `json:"filename,omitempty"`
	Extension     *string `json:"ext,omitempty"`
	AttachementId *int    `json:"tim,omitempty"`
}
type SaveFileInfo struct {
	Name      string
	Extension string
	ID        int
	Board     string
	Url       string
}
