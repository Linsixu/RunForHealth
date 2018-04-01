package magic.cn.health.event;

/**
 * @author 林思旭
 * @since 2018/3/19
 */

public class KeyboardEvent {

    private int currentType;

    private int wantType;

    public KeyboardEvent(int currentType) {
        this.currentType = currentType;
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    public int getWantType() {
        return wantType;
    }

    public void setWantType(int wantType) {
        this.wantType = wantType;
    }
}
