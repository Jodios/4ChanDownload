NAME=NotAVirus

default: build

build:
	go build -o ${NAME} github.com/jodios/4chandownload/go4chan

build_all:
	$(shell export GOOS=windows;export GOARCH=386;export CGO_ENABLED=1;export CC="i686-w64-mingw32-gcc"; go build -o $(NAME)-windows-x386.exe)
	$(shell export GOOS=windows;export GOARCH=amd64;export CGO_ENABLED=1;export CC="x86_64-w64-mingw32-gcc";go build -o $(NAME)-windows-amd64.exe)