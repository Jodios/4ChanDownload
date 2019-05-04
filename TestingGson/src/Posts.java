public class Posts {

    private long no;
    private String now;
    private String com;
    private String filename;
    private String ext;
    private int w;
    private int h;
    private int tn_w;
    private int tn_h;
    private long tim;
    private long time;
    private String md5;
    private int fsize;
    private int resto;
    private int bumplimit;
    private int imagelimit;
    private String semantic_url;
    private int replies;
    private int images;
    private int omitted_posts;
    private int omitted_images;

    public Posts(long no, String now, String com, String filename, String ext, int w, int h, int tn_w, int tn_h, long tim, long time, String md5, int fsize, int resto, int bumplimit, int imagelimit, String semantic_url, int replies, int images, int omitted_posts, int omitted_images) {
        this.no = no;
        this.now = now;
        this.com = com;
        this.filename = filename;
        this.ext = ext;
        this.w = w;
        this.h = h;
        this.tn_w = tn_w;
        this.tn_h = tn_h;
        this.tim = tim;
        this.time = time;
        this.md5 = md5;
        this.fsize = fsize;
        this.resto = resto;
        this.bumplimit = bumplimit;
        this.imagelimit = imagelimit;
        this.semantic_url = semantic_url;
        this.replies = replies;
        this.images = images;
        this.omitted_posts = omitted_posts;
        this.omitted_images = omitted_images;
    }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getTn_w() {
        return tn_w;
    }

    public void setTn_w(int tn_w) {
        this.tn_w = tn_w;
    }

    public int getTn_h() {
        return tn_h;
    }

    public void setTn_h(int tn_h) {
        this.tn_h = tn_h;
    }

    public long getTim() {
        return tim;
    }

    public void setTim(long tim) {
        this.tim = tim;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getFsize() {
        return fsize;
    }

    public void setFsize(int fsize) {
        this.fsize = fsize;
    }

    public int getResto() {
        return resto;
    }

    public void setResto(int resto) {
        this.resto = resto;
    }

    public int getBumplimit() {
        return bumplimit;
    }

    public void setBumplimit(int bumplimit) {
        this.bumplimit = bumplimit;
    }

    public int getImagelimit() {
        return imagelimit;
    }

    public void setImagelimit(int imagelimit) {
        this.imagelimit = imagelimit;
    }

    public String getSemantic_url() {
        return semantic_url;
    }

    public void setSemantic_url(String semantic_url) {
        this.semantic_url = semantic_url;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public int getOmitted_posts() {
        return omitted_posts;
    }

    public void setOmitted_posts(int omitted_posts) {
        this.omitted_posts = omitted_posts;
    }

    public int getOmitted_images() {
        return omitted_images;
    }

    public void setOmitted_images(int omitted_images) {
        this.omitted_images = omitted_images;
    }


    @Override
    public String toString() {
        return "Posts{" +
                "no=" + no +
                ", now='" + now + '\'' +
                ", com='" + com + '\'' +
                ", filename='" + filename + '\'' +
                ", ext='" + ext + '\'' +
                ", w=" + w +
                ", h=" + h +
                ", tn_w=" + tn_w +
                ", tn_h=" + tn_h +
                ", tim=" + tim +
                ", time=" + time +
                ", md5='" + md5 + '\'' +
                ", fsize=" + fsize +
                ", resto=" + resto +
                ", bumplimit=" + bumplimit +
                ", imagelimit=" + imagelimit +
                ", semantic_url='" + semantic_url + '\'' +
                ", replies=" + replies +
                ", images=" + images +
                ", omitted_posts=" + omitted_posts +
                ", omitted_images=" + omitted_images +
                '}';
    }





}
