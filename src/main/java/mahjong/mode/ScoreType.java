package mahjong.mode;

/**
 * Created by pengyi
 * Date : 16-6-12.
 */
public enum ScoreType {

    BA_GANG("扒杠", 1),
    AN_GANG("暗杠", 2),
    DIAN_GANG("点杠", 3),
    PINGHU("平胡", 4),
    ZIMO("自摸", 5),
    TIANHU("天胡", 6),
    DIHU("地胡", 7),
    FEI("飞", 8);

    private String name;
    private Integer values;

    ScoreType(String name, Integer values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValues() {
        return values;
    }

    public void setValues(Integer values) {
        this.values = values;
    }
}
