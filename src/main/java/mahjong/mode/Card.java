package mahjong.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pengyi
 * Date : 16-6-12.
 */
public class Card {
    public static int containSize(List<Integer> cardList, Integer containCard) {
        int size = 0;
        for (Integer card : cardList) {
            if (card.intValue() == containCard) {
                size++;
            }
        }
        return size;
    }

    public static List<Integer> getAllCard() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 11, 12, 13, 14, 15, 16, 17, 18, 19, 11, 12, 13, 14, 15, 16, 17, 18, 19, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 23, 24, 25, 26, 27, 28, 29, 21, 22, 23, 24, 25, 26, 27, 28, 29, 21, 22, 23, 24, 25, 26, 27, 28, 29, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                31, 33, 35, 41, 43, 45, 47, 31, 33, 35, 41, 43, 45, 47, 31, 33, 35, 41, 43, 45, 47, 31, 33, 35, 41, 43, 45, 47, 51, 53, 55, 57);
    }

    public static boolean containAll(List<Integer> cardList, List<Integer> cards) {

        for (Integer card : cards) {
            if (!cardList.contains(card)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取相同颜色的牌
     *
     * @param cardList
     * @return
     */
    public static List<Integer> getSameColor(List<Integer> cardList, int color) {

        List<Integer> sameColor = new ArrayList<>();
        List<Integer> cards = getAllSameColor(color);
        for (Integer card : cardList) {
            if (cards.contains(card)) {
                sameColor.add(card);
            }
        }
        return sameColor;
    }

    /**
     * 有相同颜色的牌
     *
     * @param color
     * @return
     */
    public static boolean hasSameColor(List<Integer> cardList, int color) {

        List<Integer> cards = getAllSameColor(color);
        for (Integer card : cardList) {
            if (cards.contains(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取相同颜色的所有牌
     *
     * @param color
     * @return
     */
    public static List<Integer> getAllSameColor(int color) {

        switch (color) {
            case 0:
                return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
            case 1:
                return Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19);
            case 2:
                return Arrays.asList(21, 22, 23, 24, 25, 26, 27, 28, 29);
            case 3:
                return Arrays.asList(31, 33, 35);
            case 4:
                return Arrays.asList(41, 43, 45, 47);
        }
        return null;
    }

    /**
     * 十三幺
     *
     * @param cardList
     * @return
     */
    public static boolean isSSY(List<Integer> cardList) {
        List<Integer> cards = Arrays.asList(1, 9, 11, 19, 21, 29, 31, 33, 35, 41, 43, 45, 47);
        return containAll(cardList, cards) && containAll(cards, cardList);
    }

    /**
     * 幺九
     *
     * @param cardList
     * @return
     */
    public static boolean isYJ(List<Integer> cardList) {
        List<Integer> cards = Arrays.asList(1, 9, 11, 19, 21, 29, 31, 33, 35, 41, 43, 45, 47);
        return containAll(cards, cardList);
    }

    /**
     * 全风
     *
     * @param cardList
     * @return
     */
    public static boolean isQF(List<Integer> cardList) {
        List<Integer> cards = Arrays.asList(41, 43, 45, 47);
        return containAll(cardList, cards) && containAll(cards, cardList);
    }
}
