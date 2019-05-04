import java.io.IOException;

public class sup{



    public static void main(String[] args) throws IOException {
        System.out.println("Deez nuts");
        download();
    }

    public static void download() throws IOException {
        for(int i = 1; i < 10; i+=3){
            Thread downloadThread = new DownloadThread(i, "/p");
            Thread downloadThread1 = new DownloadThread(i+1, "/p");
            Thread downloadThread2 = new DownloadThread(i+2, "/p");
            downloadThread.start();
            downloadThread1.start();
            downloadThread2.start();
        }

    }

}
