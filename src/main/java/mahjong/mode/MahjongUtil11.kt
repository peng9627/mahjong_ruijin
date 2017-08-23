package mahjong.mode

import java.util.HashMap
import java.util.HashSet
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.contains
import kotlin.collections.indices
import kotlin.collections.sort

/**
 * Created by pengyi
 * Date : 17-8-22.
 * desc:
 */
object MahjongUtil11 {

    fun get_dui(cardList: ArrayList<Int>): List<Int> {
        var cards = ArrayList<Int>()
        cards.addAll(cardList)
        var dui_arr = ArrayList<Int>()
        if (cards.size >= 2) {
            cards.sort()
            var i = 0
            while (i < cards.size - 1) {
                if (cards[i] == cards[i + 1]) {
                    dui_arr.add(cards[i])
                    dui_arr.add(cards[i])
                    i++
                }
                i++
            }
        }
        return dui_arr
    }

    fun get_san(cardList: ArrayList<Int>): ArrayList<Int> {
        var cards = ArrayList<Int>()
        cards.addAll(cardList)
        var san_arr = ArrayList<Int>()
        if (cards.size > 2) {
            cards.sort()
            var i = 0
            while (i < cards.size - 2) {
                if (cards[i] == cards[i + 2]) {
                    san_arr.add(cards[i])
                    san_arr.add(cards[i])
                    san_arr.add(cards[i])
                    i += 2
                }
                i++
            }
        }
        return san_arr
    }

    fun get_si(cardList: ArrayList<Int>): ArrayList<Int> {
        val cards = ArrayList<Int>()
        cards.addAll(cardList)
        val si_arr = ArrayList<Int>()
        if (cards.size > 3) {
            cards.sort()
            var i = 0
            while (i < cards.size - 3) {
                if (cards[i] == cards[i + 3]) {
                    si_arr.add(cards[i])
                    si_arr.add(cards[i])
                    si_arr.add(cards[i])
                    si_arr.add(cards[i])
                    i += 3
                }
                i++
            }
        }
        return si_arr
    }

    fun get_shun(cardList: ArrayList<Int>): ArrayList<Int> {
        val cards = ArrayList<Int>()
        cards.addAll(cardList)
        cards.sort()
        val sun_arr = ArrayList<Int>()
        val temp = ArrayList<Int>()
        temp.addAll(cards)
        while (temp.size > 2) {
            var find = false
            for (i in 0..temp.size - 2) {
                val start = temp[i]
                if (start < 30) {
                    if (temp.contains(start + 1) && temp.contains(start + 2)) {
                        sun_arr.add(start)
                        sun_arr.add(start + 1)
                        sun_arr.add(start + 2)
                        temp.remove(Integer.valueOf(start))
                        temp.remove(Integer.valueOf(start + 1))
                        temp.remove(Integer.valueOf(start + 2))
                        find = true
                        break
                    }
                } else if (start < 36) {
                    if (temp.contains(31) && temp.contains(33) && temp.contains(35)) {
                        sun_arr.add(31)
                        sun_arr.add(33)
                        sun_arr.add(35)
                        temp.remove(Integer.valueOf(31))
                        temp.remove(Integer.valueOf(33))
                        temp.remove(Integer.valueOf(35))
                        find = true
                        break
                    }
                } else {
                    var fengSize = 0
                    if (temp.contains(41)) {
                        fengSize++
                    }
                    if (temp.contains(43)) {
                        fengSize++
                    }
                    if (temp.contains(45)) {
                        fengSize++
                    }
                    if (temp.contains(47)) {
                        fengSize++
                    }

                    if (fengSize >= 3) {
                        fengSize = 0
                        if (temp.contains(41)) {
                            sun_arr.add(41)
                            temp.remove(Integer.valueOf(41))
                            fengSize++
                        }
                        if (temp.contains(43)) {
                            sun_arr.add(43)
                            temp.remove(Integer.valueOf(43))
                            fengSize++
                        }
                        if (temp.contains(45)) {
                            sun_arr.add(45)
                            temp.remove(Integer.valueOf(45))
                            fengSize++
                        }
                        if (fengSize == 3) {
                            find = true
                            break
                        }
                        if (temp.contains(47)) {
                            sun_arr.add(45)
                            temp.remove(Integer.valueOf(45))
                            find = true
                            break
                        }
                    }
                }
            }
            if (!find) {
                break
            }
        }
        return sun_arr
    }

    /**
     * 传入14张牌，判断是否可胡牌
     *
     * @param cardList
     * @return
     */
    fun hu(cardList: List<Int>, bao: Int, jiabao: Int): Boolean {

        val cards = java.util.ArrayList<Int>()
        var baoSize = 0
        if (bao > 50) {
            for (card in cardList) {
                if (card > 50) {
                    baoSize++
                } else {
                    cards.add(card)
                }
            }
            if (3 == baoSize) {
                return true
            }
        } else {
            for (card in cardList) {
                if (card > 50) {
                    cards.add(jiabao)
                } else if (card == bao) {
                    baoSize++
                } else {
                    cards.add(card)
                }
            }
            if (4 == baoSize) {
                return true
            }
        }

        val baoCan = getComputePossible(cardList, 2)

        val temp = java.util.ArrayList<Int>()
        if (0 != baoSize) {
            when (baoSize) {
                1 -> for (aBaoCan in baoCan) {
                    temp.clear()
                    temp.addAll(cards)
                    temp.add(aBaoCan)
                    if (hu(temp)) {
                        return true
                    }
                }
                2 -> for (i in baoCan.indices) {
                    temp.clear()
                    temp.addAll(cards)
                    temp.add(baoCan.get(i))
                    for (aBaoCan in baoCan) {
                        temp.add(aBaoCan)
                        if (hu(temp)) {
                            return true
                        }
                        Card.remove(temp, aBaoCan)
                    }
                }
                3 -> for (i in baoCan.indices) {
                    temp.clear()
                    temp.addAll(cards)
                    temp.add(baoCan.get(i))
                    for (j in baoCan.indices) {
                        temp.add(baoCan.get(j))
                        for (aBaoCan in baoCan) {
                            temp.add(aBaoCan)
                            if (hu(temp)) {
                                return true
                            }
                            Card.remove(temp, aBaoCan)
                        }
                        Card.remove(temp, baoCan.get(j))
                    }
                }
            }
        } else {
            return hu(cards)
        }
        return false
    }

    /**
     * 检查暗杠
     *
     * @param cards
     * @return
     */
    fun checkGang(cards: List<Int>): Int? {
        val cardList = java.util.ArrayList<Int>()
        cardList.addAll(cards)
        cardList.sort()
        for (i in 0..cardList.size - 3 - 1) {
            if (cardList[i].toInt() == cardList[i + 3]) {
                return cardList[i]
            }
        }
        return null
    }

    /**
     * 检查扒杠
     *
     * @param cards
     * @param cardList
     * @return
     */
    fun checkBaGang(cards: List<Int>, cardList: List<Int>): Int? {
        for (card in cardList) {
            for (card1 in cards) {
                if (card.toInt() == card1) {
                    return card
                }
            }
        }
        return null
    }

    /**
     * 检查吃
     *
     * @param cards
     * @param card
     * @return
     */
    fun checkChi(cards: List<Int>, card: Int?, jiabao: Int?): Boolean {
        var card = card
        val color = card!! / 10
        if (color == 5) {
            card = jiabao
        }

        val sameColor = Card.getSameColor(cards, color)

        if (jiabao!! / 10 == color) {
            val jiabaoCards = Card.getSameColor(cards, 5)

            if (0 < jiabaoCards.size) {
                for (i in jiabaoCards.indices) {
                    sameColor.add(jiabao)
                }
            }
        }

        if (color < 3) {
            if (sameColor.contains(card!! - 2) && sameColor.contains(card - 1) || sameColor.contains(card - 1) && sameColor.contains(card + 1)
                    || sameColor.contains(card + 1) && sameColor.contains(card + 2)) {
                return true
            }
        } else if (5 != color) {
            val allSameColor = HashSet<Int>()
            allSameColor.addAll(Card.getSameColor(cards, color))
            var count = 0
            for (integer in allSameColor) {
                if (integer.toInt() != card) {
                    count++
                }
                if (count == 2) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 牌型
     *
     * @param cards 手牌
     * @param bao   碰或杠的牌
     * @return
     */
    fun getHuType(cards: List<Int>, bao: Int?): ScoreType {

        val cardList = java.util.ArrayList<Int>()
        cardList.addAll(cards)

        return if (cardList.contains(bao)) {
            ScoreType.FEI
        } else ScoreType.PINGHU

    }

    /**
     * 算分
     *
     * @param scoreType
     * @return
     */
    fun getScore(scoreType: ScoreType): Int {
        when (scoreType) {
            ScoreType.TIANHU, ScoreType.DIHU, ScoreType.FEI -> return 10
        }
        return 1
    }

    private fun cardsSize(mahjongs: List<Int>): Map<Int, Int> {
        val dic = HashMap<Int, Int>()
        for (card in mahjongs) {
            if (dic.containsKey(card)) {
                dic.put(card, dic[card]!! + 1)
            } else {
                dic.put(card, 1)
            }
        }
        return dic
    }

    fun findPairNumber(mahjongs: List<Int>): Int {
        var number = 0
        var single = 0
        val dic = cardsSize(mahjongs)
        for (i in mahjongs.indices) {
            val mahjong = mahjongs[i]
            if (dic.containsKey(mahjong)) {
                val count = dic[mahjong]
                if (count!! > 1) {
                    if (count == 2 || count == 4)
                        number++
                    else
                        single++
                }
            }
        }
        return number / 2 + single / 3
    }

    fun getComputePossible(hand_list: List<Int>, number: Int): java.util.ArrayList<Int> {
        val ret = HashSet<Int>()
        for (i in hand_list.indices) {
            val mahjong = hand_list[i]
            if (!ret.contains(mahjong)) {
                ret.add(mahjong)
            }
            var stepNum = 1
            do {
                if (!ret.contains(mahjong - stepNum) && Card.legal(mahjong - stepNum)) {
                    ret.add(mahjong - stepNum)
                }
                if (!ret.contains(mahjong + stepNum) && Card.legal(mahjong + stepNum)) {
                    ret.add(mahjong + stepNum)
                }
                stepNum++
            } while (stepNum <= number)
        }
        val cards = java.util.ArrayList<Int>()
        cards.addAll(ret)
        return cards
    }

    /**
     * 传入14张牌，判断是否可胡牌
     *
     * @param cardList
     * @return
     */
    fun hu(cardList: List<Int>): Boolean {
        val handVals = java.util.ArrayList<Int>()
        handVals.addAll(cardList)
        handVals.sort()

        //检查七对
        val pairs = get_dui(handVals)
        if (pairs.size == 14) {
            return true
        }

        //检测十三幺
        if (Card.isSSY(handVals)) {
            return true
        }

        var i = 0
        while (i < pairs.size) {
            val md_val = pairs[i]
            val hand = java.util.ArrayList(handVals)
            hand.remove(Integer.valueOf(md_val))
            hand.remove(Integer.valueOf(md_val))
            if (CheckLug(hand)) {
                return true
            }
            i += 2
        }
        return false
    }

    private fun CheckLug(handVals: MutableList<Int>): Boolean {
        if (handVals.size == 0) return true
        val md_val = handVals[0]
        handVals.removeAt(0)
        if (Card.containSize(handVals, md_val) == 2) {
            handVals.remove(Integer.valueOf(md_val))
            handVals.remove(Integer.valueOf(md_val))
            return CheckLug(handVals)
        } else {
            if (handVals.contains(md_val + 1) && handVals.contains(md_val + 2)) {
                handVals.remove(Integer.valueOf(md_val + 1))
                handVals.remove(Integer.valueOf(md_val + 2))
                return CheckLug(handVals)
            }
        }
        return false
    }

}