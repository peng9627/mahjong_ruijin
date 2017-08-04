package mahjong.mode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengyi
 * Date 2017/7/28.
 */
public class OperationHistory {

    private String userName;
    private OperationHistoryType historyType;
    private List<Integer> card;

    public OperationHistory() {
    }

    public OperationHistory(String userName, OperationHistoryType historyType, List<Integer> card) {
        this.userName = userName;
        this.historyType = historyType;
        this.card = card;
    }

    public OperationHistory(String userName, OperationHistoryType historyType, Integer card) {
        this.userName = userName;
        this.historyType = historyType;
        this.card = new ArrayList<>();
        this.card.add(card);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public OperationHistoryType getHistoryType() {
        return historyType;
    }

    public void setHistoryType(OperationHistoryType historyType) {
        this.historyType = historyType;
    }

    public List<Integer> getCard() {
        return card;
    }

    public void setCard(List<Integer> card) {
        this.card = card;
    }
}
