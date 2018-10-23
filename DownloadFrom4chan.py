import requests, json, io, os, time
from PIL import Image
import urllib.request as urllib


#imagaes are at Images: http(s)://i.4cdn.org/board/tim.ext
#imagaes are at Images: https://i.4cdn.org/p/3376697.png
def countImages(data):
	count = 0
	size = 0
	for post in data['posts']:
		if 'filename' in post:
			size = size + post['fsize']
			count = count + 1
	return count, ((size/1024)/1024)

def getImages(board, threadNumber):
	url = 'https://a.4cdn.org/' + str(board) + '/thread/' + str(threadNumber) + '.json'
	r = requests.get(url)
	data = json.loads(r.text)
	imageCount, size = countImages(data)
	st = 'Downloading ' + str(round(size,2)) + 'MB of Data'#'Are you sure you want to download ' + str(imageCount) + ' pictures total size of \n' + str(round(size, 2)) + 'KB ? y/n'
	print(st)
	confirmed = 'y'#input(st)
	if confirmed == 'y':
		for post in data['posts']:
			if 'filename' in post:
				tim = post['tim']
				ext = post['ext']
				filename = post['filename'] + ext
				path = board + '/' + filename
				if ext == '.jpg' or ext == '.jpeg' or ext == '.png' and not os.path.exists(path):					
					fileURL = 'https://i.4cdn.org/' + board + '/' + str(tim) + ext
					print(fileURL)
					try:
						fd = urllib.urlopen(fileURL)
						image_file = io.BytesIO(fd.read())
						# print ('Message2')
					except:
						pass
					
					img = Image.open(image_file)				
					if not os.path.isdir(board):
						os.mkdir(board)
					img.save(path)
					
	elif confirmed == 'n':
		return


board = input("What board? ")
pageNumber = '1'
count = 0

url = 'https://a.4cdn.org/' + board + '/' + pageNumber + '.json'
r = requests.get(url)
# print ('Message1')
data = json.loads(r.text)
print(url)
print(r)
for thread in data['threads']:
	print("--------------------DIFFERENT THREAD-------------------------")
	threadNumber = thread['posts'][0]['no']
	time.sleep(1)
	getImages(board, threadNumber)





# print(r.json())