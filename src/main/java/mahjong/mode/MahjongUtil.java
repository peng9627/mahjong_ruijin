package mahjong.mode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public static boolean hu(List<Integer> cardList, Integer bao, Integer jiabao) {

        List<Integer> cards = new ArrayList<>();
        final int[] baoSize = {0};
        for (int card : cardList) {
            if (card > 50) {
                cards.add(jiabao);
            } else if (card == bao) {
                baoSize[0]++;
            } else {
                cards.add(card);
            }
        }
        if (4 == baoSize[0]) {
            return true;
        }

        List<Integer> baoCan = Card.getBaoCan();

        List<Integer> temp = new ArrayList<>();
        if (0 != baoSize[0]) {
            switch (baoSize[0]) {
                case 1:
                    for (int aBaoCan : baoCan) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(aBaoCan);
                        if (hu(temp)) {
                            return true;
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < baoCan.size(); i++) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(baoCan.get(i));
                        for (int aBaoCan : baoCan) {
                            temp.addAll(cards);
                            temp.add(aBaoCan);
                            if (hu(temp)) {
                                return true;
                            }
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < baoCan.size(); i++) {
                        temp.clear();
                        temp.addAll(cards);
                        temp.add(baoCan.get(i));
                        for (int j = 0; j < baoCan.size(); j++) {
                            temp.addAll(cards);
                            temp.add(baoCan.get(j));
                            for (int aBaoCan : baoCan) {
                                temp.addAll(cards);
                                temp.add(aBaoCan);
                                if (hu(temp)) {
                                    return true;
                                }
                            }
                        }
                    }
                    break;
            }
        } else {
            return hu(cards);
        }
        return false;
    }

    private static boolean hu(List<Integer> cards) {

        cards.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> temp = new ArrayList<>();
        temp.addAll(cards);

        //检查七对
        List<Integer> dui = get_dui(cards);
        if (dui.size() == 14) {
            return true;
        }

        //正常的胡
        temp.clear();
        temp.addAll(cards);

        //非七对先检查三个的,没个三个的都可以拆分成顺着听不同的牌
        List<Integer> san = get_san(cards);
        List<Integer> dui_temp = new ArrayList<>();
        List<Integer> cai = new ArrayList<>();
        if (0 != san.size()) {
            switch (san.size() / 3) {
                case 1:
                    //拆分三个的可能会影响听牌，先拆分
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //不拆分
                    temp.removeAll(san);
                    //拆分三个的可能会影响听牌，先拆分
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    break;

                case 2:

                    //拆分三个的可能会影响听牌，先全部拆分
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第一个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(3, 6));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第二个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 3));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //不拆分
                    temp.removeAll(san);
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    break;
                case 3:
                    //拆分三个的可能会影响听牌，先全部拆分
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分前两个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(6, 9));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第一个和第三个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(3, 6));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分后两个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 3));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第一个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(3, 9));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第二个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 3));
                    cai.removeAll(san.subList(6, 9));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第三个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 6));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }


                    //不拆分
                    temp.removeAll(san);
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    break;

                case 4:
                    //拆分三个的可能会影响听牌，先拆分
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分前三个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 3));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分后三个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(9, 12));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (i != j) {
                                cai.clear();
                                cai.addAll(temp);
                                cai.removeAll(san.subList(3 * i, 3 * i + 3));
                                cai.removeAll(san.subList(3 * j, 3 * j + 3));
                                dui = get_dui(cai);
                                dui_temp.clear();
                                for (int k = 0; k < dui.size() / 2; k++) {
                                    dui_temp.clear();
                                    dui_temp.addAll(cai);
                                    dui_temp.remove(dui.get(2 * k));
                                    dui_temp.remove(dui.get(2 * k + 1));
                                    if (dui_temp.size() == get_shun(dui_temp).size()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第一个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(3, 12));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第二个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 3));
                    cai.removeAll(san.subList(6, 12));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第三个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 6));
                    cai.removeAll(san.subList(9, 12));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //拆分三个的可能会影响听牌，拆分第四个
                    cai.clear();
                    cai.addAll(temp);
                    cai.removeAll(san.subList(0, 9));
                    dui = get_dui(cai);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(cai);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }

                    //不拆分
                    temp.removeAll(san);
                    dui = get_dui(temp);
                    dui_temp.clear();
                    for (int i = 0; i < dui.size() / 2; i++) {
                        dui_temp.clear();
                        dui_temp.addAll(temp);
                        dui_temp.remove(dui.get(2 * i));
                        dui_temp.remove(dui.get(2 * i + 1));
                        if (dui_temp.size() == get_shun(dui_temp).size()) {
                            return true;
                        }
                    }
                    break;
            }
        } else {
            //拆分三个的可能会影响听牌，拆分第一个
            dui = get_dui(temp);
            dui_temp.clear();
            for (int i = 0; i < dui.size() / 2; i++) {
                dui_temp.clear();
                dui_temp.addAll(temp);
                dui_temp.remove(dui.get(2 * i));
                dui_temp.remove(dui.get(2 * i + 1));
                if (dui_temp.size() == get_shun(dui_temp).size()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 传入手牌，找到可胡牌
     *
     * @param userCards
     * @return
     */
    public static List<Integer> ting(List<Integer> userCards) {
        List<Integer> ting_arr = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        List<Integer> allCard = Card.getAllCard();
        for (Integer card : allCard) {
            temp.clear();
            temp.addAll(userCards);
            temp.add(card);
            if (hu(temp)) {
                ting_arr.add(card);
            }
        }
        return ting_arr;
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
        cardList.sort(Integer::compareTo);
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
        int color = card % 10;
        List<Integer> sameColor = Card.getSameColor(cards, color);

        if (jiabao % 10 == color) {
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
        } else {
            List<Integer> allSameColor = Card.getAllSameColor(color);
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

    public static int CheckHu(List<Integer> handVals) {
        int ret = 0;
        List<Integer> pairs = get_dui(handVals);
        for (int i = 0; i < pairs.size(); i += 2) {
            boolean isBreak = false;
            int md_val = pairs.get(i);
            List<Integer> hand = new ArrayList<>(handVals);
            hand.remove(Integer.valueOf(md_val));
            hand.remove(Integer.valueOf(md_val));
            if (CheckLug(hand)) {
                ret = 1;
                isBreak = true;
            }
            if (isBreak) break;
        }
        return ret;
    }

    protected static boolean CheckLug(List<Integer> handVals) {
        if (handVals.size() == 0) return true;
        int md_val = handVals.get(0);
        handVals.remove(0);
        if (Card.containSize(handVals, md_val) == 2) {
            handVals.remove(Integer.valueOf(md_val));
            handVals.remove(Integer.valueOf(md_val));
            return CheckLug(handVals);
        } else {
            if (handVals.contains(md_val + 1) && handVals.contains(md_val + 2)) {
                handVals.remove(Integer.valueOf(md_val + 1));
                handVals.remove(Integer.valueOf(md_val + 2));
                return CheckLug(handVals);
            }
        }
        return false;
    }
}
