package mahjong.mode;

import java.util.*;

/**
 * Author pengyi
 * Date 17-2-14.
 */

public class MahjongUtil {

    List<Integer> cards = new ArrayList<>();

    public static List<Integer> dealIntegers(List<Integer> allIntegers) {
        List<Integer> cardList = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            int cardIndex = (int) (Math.random() * allIntegers.size());
            cardList.add(allIntegers.get(cardIndex));
            allIntegers.remove(cardIndex);
        }
        return cardList;
    }

    public static List<Integer> get_dui(List<Integer> cardList) {
        List<Integer> cards = new ArrayList<>();
        cards.addAll(cardList);
        List<Integer> dui_arr = new ArrayList<>();
        if (cards.size() >= 2) {
            cards.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            for (int i = 0; i < cards.size() - 1; i++) {
                if (cards.get(i).intValue() == cardList.get(i + 1).intValue()) {
                    dui_arr.add(cards.get(i));
                    dui_arr.add(cards.get(i));
                    i++;
                }
            }
        }
        return dui_arr;
    }

    public static List<Integer> get_san(List<Integer> cardList) {
        List<Integer> cards = new ArrayList<>();
        cards.addAll(cardList);
        List<Integer> san_arr = new ArrayList<>();
        if (cards.size() >= 3) {
            cards.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            for (int i = 0; i < cards.size() - 2; i++) {
                if (cards.get(i).intValue() == cards.get(i + 2).intValue()) {
                    san_arr.add(cards.get(i));
                    san_arr.add(cards.get(i));
                    san_arr.add(cards.get(i));
                    i += 2;
                }
            }
        }
        return san_arr;
    }

    public static List<Integer> get_si(List<Integer> cardList) {
        List<Integer> cards = new ArrayList<>();
        cards.addAll(cardList);
        List<Integer> san_arr = new ArrayList<>();
        if (cards.size() >= 4) {
            cards.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            for (int i = 0; i < cards.size() - 3; i++) {
                if (cards.get(i).intValue() == cards.get(i + 3).intValue()) {
                    san_arr.add(cards.get(i));
                    san_arr.add(cards.get(i));
                    san_arr.add(cards.get(i));
                    san_arr.add(cards.get(i));
                    i += 3;
                }
            }
        }
        return san_arr;
    }

    public static List<Integer> get_shun(List<Integer> cardList) {
        List<Integer> cards = new ArrayList<>();
        cards.addAll(cardList);
        cards.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> sun_arr = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        temp.addAll(cards);
        while (temp.size() > 2) {
            boolean find = false;
            for (int i = 0; i < temp.size() - 2; i++) {
                int start = temp.get(i);
                if (temp.get(i) < 30) {
                    if (temp.contains(start + 1) && temp.contains(start + 2)) {
                        sun_arr.add(start);
                        sun_arr.add(start + 1);
                        sun_arr.add(start + 2);
                        temp.remove(Integer.valueOf(start));
                        temp.remove(Integer.valueOf(start + 1));
                        temp.remove(Integer.valueOf(start + 2));
                        find = true;
                        break;
                    }
                } else if (temp.get(i) < 36) {//中发白
                    if (temp.contains(31) && temp.contains(33) && temp.contains(35)) {
                        sun_arr.add(31);
                        sun_arr.add(33);
                        sun_arr.add(35);
                        temp.remove(Integer.valueOf(31));
                        temp.remove(Integer.valueOf(33));
                        temp.remove(Integer.valueOf(35));
                        find = true;
                        break;
                    }
                } else {//东南西北
                    int fengSize = 0;
                    if (temp.contains(41)) {
                        fengSize++;
                    }
                    if (temp.contains(43)) {
                        fengSize++;
                    }
                    if (temp.contains(45)) {
                        fengSize++;
                    }
                    if (temp.contains(47)) {
                        fengSize++;
                    }

                    if (fengSize >= 3) {
                        fengSize = 0;
                        if (temp.contains(41)) {
                            sun_arr.add(41);
                            temp.remove(Integer.valueOf(41));
                            fengSize++;
                        }
                        if (temp.contains(43)) {
                            sun_arr.add(43);
                            temp.remove(Integer.valueOf(43));
                            fengSize++;
                        }
                        if (temp.contains(45)) {
                            sun_arr.add(45);
                            temp.remove(Integer.valueOf(45));
                            fengSize++;
                        }
                        if (fengSize == 3) {
                            find = true;
                            break;
                        }
                        if (temp.contains(47)) {
                            sun_arr.add(45);
                            temp.remove(Integer.valueOf(45));
                            find = true;
                            break;
                        }
                    }

                }
            }
            if (!find) {
                break;
            }
        }
        return sun_arr;
    }

    /**
     * 传入14张牌，判断是否可胡牌
     *
     * @param cardList
     * @return
     */
    public static boolean fei(List<Integer> cardList, Integer bao, Integer jiabao) {

        List<Integer> cards = new ArrayList<>();
        int baoSize = 0;
        if (bao > 50) {
            for (int card : cardList) {
                if (card > 50) {
                    baoSize++;
                } else {
                    cards.add(card);
                }
            }
            if (3 == baoSize) {
                return true;
            }
        } else {
            for (int card : cardList) {
                if (card > 50) {
                    cards.add(jiabao);
                } else if (card == bao) {
                    baoSize++;
                } else {
                    cards.add(card);
                }
            }
            if (4 == baoSize) {
                return true;
            }
        }

        List<Integer> baoCan = getComputePossible(cards, 2);

        List<Integer> temp = new ArrayList<>();
        if (0 != baoSize) {
            switch (baoSize) {
                case 1:
                    for (int aBaoCan : baoCan) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(aBaoCan);
                        if (hu(temp, true)) {
                            return true;
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < baoCan.size(); i++) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(baoCan.get(i));
                        for (int j = i; j < baoCan.size(); j++) {
                            temp.add(baoCan.get(j));
                            if (hu(temp, true)) {
                                return true;
                            }
                            Card.remove(temp, baoCan.get(j));
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < baoCan.size(); i++) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(baoCan.get(i));
                        for (int j = 0; j < baoCan.size(); j++) {
                            temp.add(baoCan.get(j));
                            for (int k = j; k < baoCan.size(); k++) {
                                temp.add(baoCan.get(k));
                                if (hu(temp, true)) {
                                    return true;
                                }
                                Card.remove(temp, baoCan.get(k));
                            }
                            Card.remove(temp, baoCan.get(j));
                        }
                    }
                    break;
            }
        } else {
            return hu(cards, true);
        }
        return false;
    }

    /**
     * 检查暗杠
     *
     * @param cards
     * @return
     */
    public static Integer checkGang(List<Integer> cards) {
        List<Integer> cardList = new ArrayList<>();
        cardList.addAll(cards);
        cardList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        for (int i = 0; i < cardList.size() - 3; i++) {
            if (cardList.get(i).intValue() == cardList.get(i + 3)) {
                return cardList.get(i);
            }
        }
        return null;
    }

    /**
     * 检查扒杠
     *
     * @param cards
     * @param cardList
     * @return
     */
    public static Integer checkBaGang(List<Integer> cards, List<Integer> cardList) {
        for (Integer card : cardList) {
            for (Integer card1 : cards) {
                if (card.intValue() == card1) {
                    return card;
                }
            }
        }
        return null;
    }

    /**
     * 检查吃
     *
     * @param cards
     * @param card
     * @return
     */
    public static boolean checkChi(List<Integer> cards, Integer card, Integer jiabao) {
        int color = card / 10;
        if (color == 5) {
            card = jiabao;
        }
        color = card / 10;
        List<Integer> sameColor = Card.getSameColor(cards, color);

        if (jiabao / 10 == color) {
            List<Integer> jiabaoCards = Card.getSameColor(cards, 5);

            if (0 < jiabaoCards.size()) {
                for (int i = 0; i < jiabaoCards.size(); i++) {
                    sameColor.add(jiabao);
                }
            }
        }

        if (color < 3) {
            if ((sameColor.contains(card - 2) && sameColor.contains(card - 1)) || (sameColor.contains(card - 1) && sameColor.contains(card + 1))
                    || (sameColor.contains(card + 1) && sameColor.contains(card + 2))) {
                return true;
            }
        } else if (5 != color) {
            Set<Integer> allSameColor = new HashSet<>();
            allSameColor.addAll(Card.getSameColor(sameColor, color));
            int count = 0;
            for (Integer integer : allSameColor) {
                if (integer.intValue() != card) {
                    count++;
                }
                if (count == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 牌型
     *
     * @param cards 手牌
     * @param bao   碰或杠的牌
     * @return
     */
    public static ScoreType getHuType(List<Integer> cards, Integer bao) {

        List<Integer> cardList = new ArrayList<>();
        cardList.addAll(cards);

        if (cardList.contains(bao)) {
            return ScoreType.FEI;
        }

        return ScoreType.PINGHU;
    }

    /**
     * 算分
     *
     * @param scoreType
     * @return
     */
    public static int getScore(ScoreType scoreType) {
        switch (scoreType) {
            case TIANHU:
            case DIHU:
            case FEI:
                return 10;
        }
        return 1;
    }

    private static Map<Integer, Integer> cardsSize(List<Integer> mahjongs) {
        Map<Integer, Integer> dic = new HashMap<>();
        for (Integer card : mahjongs) {
            if (dic.containsKey(card)) {
                dic.put(card, dic.get(card) + 1);
            } else {
                dic.put(card, 1);
            }
        }
        return dic;
    }

    public static int findPairNumber(List<Integer> mahjongs) {
        int number = 0;
        int single = 0;
        Map<Integer, Integer> dic = cardsSize(mahjongs);
        for (int i = 0; i < mahjongs.size(); i++) {
            int mahjong = mahjongs.get(i);
            if (dic.containsKey(mahjong)) {
                int count = dic.get(mahjong);
                if (count > 1) {
                    if (count == 2 || count == 4) number++;
                    else single++;
                }
            }
        }
        return number / 2 + single / 3;
    }

    public static ArrayList<Integer> getComputePossible(List<Integer> hand_list, int number) {
        Set<Integer> ret = new HashSet<>();
        for (int i = 0; i < hand_list.size(); i++) {
            int mahjong = hand_list.get(i);
            if (!ret.contains(mahjong)) {
                ret.add(mahjong);
            }
            int stepNum = 1;
            do {
                if (!ret.contains(mahjong - stepNum) && Card.legal(mahjong - stepNum)) {
                    ret.add(mahjong - stepNum);
                }
                if (!ret.contains(mahjong + stepNum) && Card.legal(mahjong + stepNum)) {
                    ret.add(mahjong + stepNum);
                }
                stepNum++;
            } while (stepNum <= number);
        }
        ArrayList<Integer> cards = new ArrayList<>();
        cards.addAll(ret);
        return cards;
    }

    /**
     * 传入14张牌，判断是否可胡牌
     *
     * @param cardList
     * @return
     */
    public static boolean hu(List<Integer> cardList, boolean fei) {
        List<Integer> handVals = new ArrayList<>();
        handVals.addAll(cardList);
        handVals.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        //检查七对
        List<Integer> pairs = get_dui(handVals);
        if (pairs.size() == 14) {
            return true;
        }

        //检测十三幺
        if (Card.isSSY(handVals)) {
            return true;
        }

        for (int i = 0; i < pairs.size(); i += 2) {
            int md_val = pairs.get(i);
            List<Integer> hand = new ArrayList<>(handVals);
            hand.remove(Integer.valueOf(md_val));
            hand.remove(Integer.valueOf(md_val));
            if (CheckLug(hand, fei)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean CheckLug(List<Integer> handVals, boolean fei) {
        if (handVals.size() == 0) return true;
        int md_val = handVals.get(0);
        handVals.remove(0);
        if (Card.containSize(handVals, md_val) == 2) {
            handVals.remove(Integer.valueOf(md_val));
            handVals.remove(Integer.valueOf(md_val));
            return CheckLug(handVals, fei);
        } else {
            if (fei) {
                if (handVals.contains(md_val + 1) && handVals.contains(md_val + 2)) {
                    handVals.remove(Integer.valueOf(md_val + 1));
                    handVals.remove(Integer.valueOf(md_val + 2));
                    return CheckLug(handVals, fei);
                }
            } else {
                if (md_val < 30) {
                    if (handVals.contains(md_val + 1) && handVals.contains(md_val + 2)) {
                        handVals.remove(Integer.valueOf(md_val + 1));
                        handVals.remove(Integer.valueOf(md_val + 2));
                        return CheckLug(handVals, fei);
                    }
                } else {
                    if (handVals.contains(md_val + 2) && handVals.contains(md_val + 4)
                            && 3 != Card.containSize(handVals, md_val + 2)
                            && 3 != Card.containSize(handVals, md_val + 4)) {
                        handVals.remove(Integer.valueOf(md_val + 2));
                        handVals.remove(Integer.valueOf(md_val + 4));
                        return CheckLug(handVals, false);
                    }
                    if (handVals.contains(md_val + 4) && handVals.contains(md_val + 6)
                            && 3 != Card.containSize(handVals, md_val + 4)
                            && 3 != Card.containSize(handVals, md_val + 6)) {
                        handVals.remove(Integer.valueOf(md_val + 4));
                        handVals.remove(Integer.valueOf(md_val + 6));
                        return CheckLug(handVals, false);
                    }
                    if (handVals.contains(md_val + 2) && handVals.contains(md_val + 6)
                            && 3 != Card.containSize(handVals, md_val + 2)
                            && 3 != Card.containSize(handVals, md_val + 6)) {
                        handVals.remove(Integer.valueOf(md_val + 2));
                        handVals.remove(Integer.valueOf(md_val + 6));
                        return CheckLug(handVals, false);
                    }
                }
            }

        }
        return false;
    }
}
