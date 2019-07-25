package application;

public class TileProperties {
    Integer x;
    Integer y;
    Long num;

    public TileProperties(Integer x, Integer y, Long num) {
        this.x = x;
        this.y = y;
        this.num = num;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }
}
