package mahjong.mode;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by pengyi
 * Date : 17-8-22.
 * desc:
 */
public class JxCalculate {

    private List<Integer> handList;
    private Integer lai_majong;
    /**
     * 癞子牌的计算数据
     */
    private Map<Integer, JxCalculateData> calMap = new HashMap<Integer, JxCalculateData>();
    private boolean general = false;
    private List<Integer> c_huList = new ArrayList<Integer>();

    public JxCalculate(List<Integer> handList, Integer lai_majong) {
        this.handList = handList;
        this.lai_majong = lai_majong;
    }

    public void calculateHu() {
        general = false;
        List<Integer> partition_1 = new ArrayList<>();
        List<Integer> partition_2 = new ArrayList<>();
        for (int i : handList) {
            if (i == lai_majong) {
                partition_1.add(i);
            } else {
                partition_2.add(i);
            }
        }

        if (partition_1.isEmpty()) {
//                super.calculateHu()
//                if (c_huList.isNotEmpty()) {
//                    hunMajong(player.handList, gameData.lai_majong, gameData.lai_majong, 1)
//                    c_huList.add(gameData.lai_majong)
//                }
        } else {
            long now = System.currentTimeMillis();
            calMap.clear();
            c_huList.clear();
            //剔除混子牌
            List<Integer> handlist = new ArrayList<>();
            handlist.addAll(partition_2);
            if (handlist.isEmpty() && partition_1.size() == 1) {
                //只有一张癞子牌
                general = true;
                return;
            }
            handlist.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            List<Integer> possible = MahjongUtil.getComputePossible(handlist, 2);
            Card.remove(possible, lai_majong);
            hunMajong(handlist, possible, lai_majong, partition_1.size());
            if (0 == calMap.size()) {
                hunMajong(handlist, lai_majong, lai_majong, partition_1.size() + 1);
            }
            calMap.forEach(new BiConsumer<Integer, JxCalculateData>() {
                @Override
                public void accept(Integer integer, JxCalculateData jxCalculateData) {
                    c_huList.add(integer);
                    System.out.println("win: " + jxCalculateData.hu_majong);
                    System.out.println("  ===  ");
                    System.out.println(jxCalculateData.list);
                    System.out.println("  ===  ");
                    System.out.println(jxCalculateData.replace);
                }
            });
            System.out.println("time: " + (System.currentTimeMillis() - now));
        }
//        }
    }

    private void hunMajong(List<Integer> handlist, Integer hu_majong, Integer hun_majong, Integer num) {
        int count = MahjongUtil.findPairNumber(handlist);
        if (count + num > 6) {
            JxCalculateData calData = new JxCalculateData();
            calData.hu_majong = hu_majong;
            handlist.forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    Integer majong = integer;
                    calData.list.add(majong);
                    int c = Card.containSize(handList, majong);
                    if (c == 1 || c == 3) {
                        if (!calData.replace.contains(majong)) {
                            calData.replace.add(majong);
                            calData.list.add(majong);
                        }
                    }
                }
            });
            while (calData.list.size() < 14) {
                calData.list.add(hun_majong);
            }
            calData.list.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            calMap.put(hu_majong, calData);
        } else {
            handlist.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            //所有能做将的可能
            List<Integer> pairs = new ArrayList<>();
            handlist.forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    if (!pairs.contains(integer)) {
                        pairs.add(integer);
                    }
                }
            });
            List<Integer> list = new ArrayList<>();
            List<Integer> replace = new ArrayList<>();
            for (Integer pair : pairs) {
                int hun = num;
                replace.clear();
                if (Card.containSize(handlist, pair) == 1) {
                    if (hun == 0) {
                        return;
                    }
                    replace.add(pair);
                    hun--;
                }
                copylist(handlist, list, pair);
                if (lug(list, replace, hun)) {
                    JxCalculateData calData = new JxCalculateData();
                    calData.hu_majong = hu_majong;
                    calData.list.addAll(replace);
                    calData.list.addAll(handlist);
                    calData.list.sort(new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    calData.replace.addAll(replace);
                    calMap.put(hu_majong, calData);
                    return;
                }
            }
        }
        return;
    }

    private void hunMajong(List<Integer> handlist, List<Integer> possible, Integer hun_majong, Integer num) {
        //进入这个方法，就代表手上一定有混牌  留一张混牌做将，先测试万能胡牌
        int count = MahjongUtil.findPairNumber(handlist);
        if (count + num > 6) {
            //7对的万能牌
            general = true;
            return;
        }
        List<Integer> temp = new ArrayList<>();
        temp.addAll(handlist);
        //留下一张混牌当将，万能胡
        if (lug(temp, new ArrayList<Integer>(), num - 1)) {
            general = true;
            return;
        }
        possible.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                handlist.add(integer);
                hunMajong(handlist, integer, hun_majong, num);
                handlist.remove(integer);
            }
        });
    }

    //同一颜色，不是风牌
    private boolean subLug(Integer majong, List<Integer> list, List<Integer> replace, int hun) {
        if (list.isEmpty()) {
            if (hun > 1) {
                replace.add(majong);
                replace.add(majong);
                return true;
            }
            return false;
        }
        if (list.size() == 1) {
            if (hun > 0) {
                int only = list.remove(0);
                if (majong == only) {
                    replace.add(majong);
                    return true;
                }
                if (only - majong < 3) {
                    if (majong + 1 == only) {
                        if (Card.legal(only + 1))
                            replace.add(only + 1);
                        else
                            replace.add(majong - 1);
                    } else
                        replace.add(majong + 1);
                    return true;
                }
            }
            return false;
        }
        int count = Card.containSize(list, majong);
        switch (count) {
            case 0:
                //一定要连成顺子 否则消耗2张混牌
                Integer next = list.get(0);
                if (next - majong < 3) {
                    if (majong + 2 == next) {
                        if (hun > 0) {
                            replace.add(majong + 1);
                            list.remove(next);
                            return subLug(list.remove(0), list, replace, hun - 1);
                        } else return false;
                    } else {
                        //两张牌相邻
                        Integer want = next + 1;
                        if (Card.legal(want)) {
                            if (list.contains(want)) {
                                list.remove(next);
                                list.remove(want);
                                if (!list.isEmpty()) {
                                    return subLug(list.remove(0), list, replace, hun);
                                }
                                return true;
                            } else {
                                if (hun > 0) {
                                    replace.add(want);
                                    list.remove(next);
                                    return subLug(list.remove(0), list, replace, hun - 1);
                                } else {
                                    return false;
                                }
                            }
                        } else {
                            if (hun > 0) {
                                replace.add(majong - 1);
                                list.remove(next);
                                return subLug(list.remove(0), list, replace, hun - 1);
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    if (hun > 1) {
                        replace.add(majong);
                        replace.add(majong);
                        return subLug(list.remove(0), list, replace, hun - 2);
                    }
                }
                if (list.contains(majong + 1)) {
                    if (list.contains(majong + 2)) {
                        list.remove(majong + 1);
                        list.remove(majong + 2);
                        if (list.isEmpty()) {
                            return true;
                        }
                        return subLug(list.remove(0), list, replace, hun);
                    } else {
                        if (hun > 0) {
                            list.remove(majong + 1);
                            if (subLug(list.remove(0), list, replace, hun - 1)) {
                                int hun_majong = majong + 2;
                                if (Card.legal(hun_majong)) {
                                    replace.add(hun_majong);
                                } else {
                                    replace.add(majong - 1);
                                }
                            }
                        }
                        return false;
                    }
                }
                break;
            case 1:
                //两种可能，混牌成3张或者找顺子，都要判断
                next = list.get(1);
                if (next - majong < 3) {//有顺子的可能
                    if (majong + 2 == next) {
                        if (hun > 0) {
                            List<Integer> clist = new ArrayList<>();
                            clist.addAll(list);
                            clist.remove(next);
                            if (subLug(clist.remove(0), clist, replace, hun - 1)) {
                                replace.add(majong + 1);
                                return true;
                            }
                        }
                    } else {
                        //两张牌相邻
                        int want = next + 1;
                        if (Card.legal(want)) {
                            List<Integer> clist = new ArrayList<>();
                            clist.addAll(list);
                            if (clist.contains(want)) {
                                clist.remove(next);
                                clist.remove(want);
                                if (subLug(clist.remove(0), clist, replace, hun))
                                    return true;
                            } else {
                                if (hun > 0) {
                                    clist.remove(next);
                                    if (subLug(clist.remove(0), clist, replace, hun - 1)) {
                                        replace.add(want);
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (hun > 0) {
                                List<Integer> clist = new ArrayList<>();
                                clist.addAll(list);
                                clist.remove(next);
                                if (subLug(clist.remove(0), clist, replace, hun - 1)) {
                                    replace.add(majong - 1);
                                    return true;
                                }
                            }
                        }
                    }
                }
                //没法凑顺子，直接3张
                if (hun > 0) {
                    list.remove(0);
                    replace.add(majong);
                    return subLug(list.remove(0), list, replace, hun - 1);
                } else {
                    return false;
                }
            default:
                list.remove(majong);
                list.remove(majong);
                if (!list.isEmpty()) {
                    return subLug(list.remove(0), list, replace, hun);
                }
                return true;
        }
        return false;
    }

    private boolean subFengLug(int majong, List<Integer> list, List<Integer> replace, int hun)

    {
        int count = Card.containSize(list, majong);
        switch (count) {
            case 0:
                if (hun > 1) {
                    replace.add(majong);
                    replace.add(majong);
                    if (!list.isEmpty()) {
                        return subFengLug(list.remove(0), list, replace, hun - 2);
                    }
                    return true;
                }
                break;
            case 1:
                if (hun > 0) {
                    replace.add(majong);
                    list.remove(majong);
                    if (!list.isEmpty()) {
                        return subFengLug(list.remove(0), list, replace, hun - 1);
                    }
                    return true;
                }
                break;
            default:
                list.remove(majong);
                list.remove(majong);
                if (!list.isEmpty()) {
                    return subFengLug(list.remove(0), list, replace, hun);
                }
                return true;
        }
        return false;
    }

    private boolean lug(List<Integer> list, List<Integer> replace, int hun)

    {
        if (list.isEmpty()) {
            return true;
        }
        int majong = list.remove(0);
        int color = majong / 10;
        List<Integer> partition_1 = new ArrayList<>();
        List<Integer> partition_2 = new ArrayList<>();
        for (int i : list) {
            if (i / 10 == color) {
                partition_1.add(i);
            } else {
                partition_2.add(i);
            }
        }

        List<Integer> sublist = partition_1;
        int number = hun + replace.size();
        if (2 < color) {
            if (subFengLug(majong, sublist, replace, hun)) {
                return lug(partition_2, replace, number - replace.size());
            }
        } else {
            if (subLug(majong, sublist, replace, hun)) {
                return lug(partition_2, replace, number - replace.size());
            }
        }
        return false;
    }

    private void copylist(List<Integer> source, List<Integer> target, Integer majong) {
        final int[] number = {2};
        target.clear();
        source.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                if (integer == majong.intValue() && number[0] > 0) {
                    number[0] -= 1;
                } else {
                    target.add(integer);
                }
            }
        });
    }

    class JxCalculateData {
        /**
         * 胡的那张牌
         */
        Integer hu_majong = 0;

        /**
         * 胡牌是对应的手牌
         */
        List<Integer> list = new ArrayList<>();

        /**
         * 替换的牌
         */
        List<Integer> replace = new ArrayList<>();
    }
}


