import requests, json, io, os, time, glob, re
import urllib.request as urllib
import threading



def unhtml(string):
    # replace <tag>...</tag>, possibly more than once
    done = False
    while not done:
        temp = re.sub(r'<([^/]\S*)[^>]*>[\s\S]*?</\1>', '', string)
        done = temp == string
        string = temp
    # replace remaining standalone tags, if any
    string = re.sub(r'<[^>]*>', '', string)
    string = re.sub(r'\s{2,}', ' ', string)
    return string.strip()

def countImages(data):
    count = 0
    size = 0
    for post in data['posts']:
        if 'filename' in post:
            size = size + post['fsize']
            count = count + 1
    return count, ((size/1024)/1024)



def checkComment(textPath, comment):
    f = open(textPath, 'r')
    comment = unhtml(comment)
    for line in f:
        if comment in line or line in comment:
            return True
    return False

def addToFile(comment, threadFile, postNumber, textPath, filename):
    commentExists = checkComment(textPath, comment)

def fileExists(path, myDir):
    list_of_files = glob.glob(myDir+"*")
    name = path.split('/')
    name = name[3]
    name = name.split('_')
    name = name[3].strip()
    for file in list_of_files:
        if not '.txt' in file and not '.json' in file:
            exfile = file.split('/')
            if len(exfile) < 4:
                exfile = exfile + exfile[2].split('\\')
                del(exfile[2])
            exfile = exfile[3]
            exfile = exfile.split("_")
            exfile = exfile[3].strip()
            if name in exfile or exfile in name:
                return True
    return False

def saveImage(url, path, iCount):
    try:
        image = urllib.URLopener()
        image.retrieve(url, path)
    except:
        print("Error saving image")
        iCount -= 1

def getLatest(fileList):
    numbers = []
    for file in fileList:
        if not ('.txt' in file) and not ('.json' in file):
            file = file.split('/')
            #print("*****************", len(file))
            if len(file) < 4:
                file = file + file[2].split("\\")
                del(file[2])            
            #print("*****************", len(file))
            file = file[3]
            file = file.split('_')
            numbers.append(int(file[0]))
    numbers.sort()
    print(numbers)
    if not numbers:
        return 0
    return(numbers[len(numbers)-1])

def archiveThread(board, threadNumber, iCount):
    url = 'https://a.4cdn.org/' + str(board) + '/thread/' + str(threadNumber) + '.json'
    r = requests.get(url)
    t = 0
    myDir = "Archives"+"/"+board+"/"+str(threadNumber) + "/"
    if not os.path.isdir(myDir):
        os.makedirs(myDir)
    try:
        data = json.loads(r.text)
        imageCount, size = countImages(data)
        filename = ""
    except:
        return


    for post in data['posts']:
        if 'filename' in post:
            list_of_files = glob.glob(myDir+"*")
            if list_of_files and t is 0:
                iCount = getLatest(list_of_files)
                t += 1

            tim = post['tim']
            ext = post['ext']
            iCount += 1
            filename = str(iCount).zfill(3) + "_" + board + '_' + str(threadNumber) + '_' + post['filename'] + ext
            path = myDir + filename

            if (ext == '.gif' or ext =='.png' or ext=='.jpg') and ( fileExists(path, myDir) is False):					
                fileURL = 'https://i.4cdn.org/' + board + '/' + str(tim) + ext
                saveImage(fileURL, path, iCount)
            else:
                iCount -= 1
        else:
            filename = ""

        textPath = myDir + "0_" + str(threadNumber)+".txt"
        threadFile = open(textPath, 'a')	
        if 'com' in post:
            addToFile(post['com'], threadFile, post['no'], textPath, filename)
        else:
            addToFile("---------------------------------NO COMMENT----------------------------------", threadFile, post['no'], textPath, filename)

def start_(board, thread, iCount):    
    print("--------------------DIFFERENT THREAD-------------------------")
    threadNumber = thread['posts'][0]['no']
    print(threadNumber)
    time.sleep(1)
    iCount = 0
    archiveThread(board, threadNumber, iCount)
    iCount = 0
    time.sleep(1)
    
    
board = input("What board? ")
pages = 10
count = 0
while True:
    for pageNumber in range(1,int(pages)):
        url = 'https://a.4cdn.org/' + board + '/' + str(pageNumber) + '.json'
        r = requests.get(url)
        data = json.loads(r.text)
        print(url)
        thread1 = threading.Thread()
        thread2 = threading.Thread()
        thread3 = threading.Thread()
        for thread in range(1, len(data['threads']), 3):
            if(thread+2 < 15):
                thread1 = threading.Thread(target=start_, args=(board, data['threads'][thread], 1, ))
                thread2 = threading.Thread(target=start_, args=(board, data['threads'][thread+1], 1, ))
                thread3 = threading.Thread(target=start_, args=(board, data['threads'][thread+2], 1, ))
                thread1.start()
                thread2.start()
                thread3.start()
                thread1.join()
                thread2.join()
                thread3.join()
            else:
                thread1 = threading.Thread(target=start_, args=(board, data['threads'][thread], 1, ))
                thread2 = threading.Thread(target=start_, args=(board, data['threads'][thread+1], 1, ))
                thread1.start()
                thread2.start()
                thread1.join()
                thread2.join()
            time.sleep(1)
