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

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/dialog"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
)

const (
	url            = "https://a.4cdn.org"
	imagesUrl      = "https://i.4cdn.org"
	maxConcurrency = 30
)

var (
	posts []Post
	lock  = &sync.Mutex{}
	board = "gif"
	//go:embed boards.json
	BOARDS      []byte
	downloading = false
	total       = 0
	downloaded  = 0
	directory   = ""
)
var progress *widget.ProgressBar

type Board struct {
	Board string `json:"board"`
	Title string `json:"title"`
}

func main() {
	directory, _ = os.UserHomeDir()
	boards := struct {
		Boards []Board `json:"boards"`
	}{}
	err := json.Unmarshal(BOARDS, &boards)
	if err != nil {
		log.Fatal(err)
	}
	boardMap := make(map[string]string)
	boardTitles := make([]string, 0)
	for _, b := range boards.Boards {
		boardTitles = append(boardTitles, b.Title)
		boardMap[b.Title] = b.Board
	}
	board = boards.Boards[0].Board

	downloadApp := app.New()
	w := downloadApp.NewWindow("4chan Download haha lol")
	w.Resize(fyne.Size{
		Width:  500,
		Height: 500,
	})

	downloadButton := widget.NewButton("Download", func() {
		go StartDownload()
	})
	dropDown := widget.NewSelect(boardTitles, func(chosen string) {
		board = boardMap[chosen]
	})
	progress = widget.NewProgressBar()

	folderDialog := dialog.NewFolderOpen(func(lu fyne.ListableURI, err error) {
		directory = lu.Path()
		fmt.Println(lu.Fragment())
		fmt.Println(lu.Query())
	}, w)
	dialogOpenButton := widget.NewButton(directory, func() {
		folderDialog.Show()
	})
	go func() {
		for {
			dialogOpenButton.SetText(directory)
		}
	}()

	grid := container.New(layout.NewGridLayout(1), dialogOpenButton, dropDown, downloadButton, progress)
	w.SetContent(grid)

	w.ShowAndRun()
}

func StartDownload() {
	if downloading {
		return
	}
	downloading = true
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
	total = len(posts)

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
	downloading = false
	progress.SetValue(0)
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
		Timeout: time.Second * 50,
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
		fmt.Println(err)
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
		filePath := directory + "/" + saveInfo.Board + "/" + saveInfo.Name + saveInfo.Extension
		if _, err := os.Stat(directory + "/" + saveInfo.Board); errors.Is(err, os.ErrNotExist) {
			if err := os.Mkdir(directory+"/"+saveInfo.Board, fs.ModePerm); err != nil {
				fmt.Println(err)
			}
		}
		if _, err := os.Stat(filePath); err == nil {
			IncrementDownloaded()
			continue
		}
		Write(filePath, Get(saveInfo.Url))
		IncrementDownloaded()
	}
}
func AppendPost(p []Post) {
	lock.Lock()
	defer lock.Unlock()
	posts = append(posts, p...)
}
func IncrementDownloaded() {
	lock.Lock()
	downloaded++
	progress.SetValue(float64(downloaded) / float64(total))
	lock.Unlock()
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
