import requests, json, io, os, time, glob, re, threading
import urllib.request as urllib


thread1 = threading.Thread()
thread2 = threading.Thread()
thread3 = threading.Thread()
board = input("What board? ")
print("Downloading Everything from {}!".format(board))
pages = 10
count = 0


def fileExists(path, myDir):
    list_of_files = glob.glob(myDir+"*")
    name = path.split('/')
    name = name[3]
    #print(name)
    name = name.split('_')
    #print(name)
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
    #print(numbers)
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

def _start(board, url):
    r = requests.get(url)
    data = json.loads(r.text)
    for thread in data['threads']:
        #print("--------------------DIFFERENT THREAD-------------------------")
        threadNumber = thread['posts'][0]['no']
        #print(threadNumber)
        time.sleep(1)
        iCount = 0
        archiveThread(board, threadNumber, iCount)
        iCount = 0
    pass

while True:
    for pageNumber in range(1,int(pages), 3):
        url1 = 'https://a.4cdn.org/' + board + '/' + str(pageNumber) + '.json'
        url2 = 'https://a.4cdn.org/' + board + '/' + str(pageNumber+1) + '.json'
        url3 = 'https://a.4cdn.org/' + board + '/' + str(pageNumber+2) + '.json'
        thread1 = threading.Thread(target=_start, args=(board, url1, ))
        thread2 = threading.Thread(target=_start, args=(board, url2, ))
        thread3 = threading.Thread(target=_start, args=(board, url3, ))
        thread1.start()
        thread2.start()
        thread3.start()
        thread1.join()
        thread2.join()
        thread3.join()

