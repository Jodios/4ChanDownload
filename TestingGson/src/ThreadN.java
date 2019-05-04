import java.util.ArrayList;

public class ThreadN {

    //array of posts
    private ArrayList<Posts> posts;

    public ThreadN(ArrayList<Posts> posts) {
        this.posts = posts;
    }

    public ArrayList<Posts> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Posts> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "ThreadN{" +
                "posts=" + posts +
                '}';
    }
}
