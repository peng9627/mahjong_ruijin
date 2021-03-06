package mahjong.mode;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import mahjong.constant.Constant;
import mahjong.entrance.MahjongTcpService;
import mahjong.redis.RedisService;
import mahjong.timeout.OperationTimeout;
import mahjong.timeout.PlayCardTimeout;
import mahjong.timeout.ReadyTimeout;
import mahjong.utils.HttpUtil;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Author pengyi
 * Date 17-3-7.
 */
public class Room {

    private int baseScore; //基础分
    private String roomNo;  //桌号
    private List<Seat> seats = new ArrayList<>();//座位
    private List<Integer> seatNos;
    private int operationSeatNo;
    private List<OperationHistory> historyList = new ArrayList<>();
    private List<Integer> surplusCards;//剩余的牌
    private GameStatus gameStatus;

    private int lastOperation;

    private int banker;//庄家
    private int gameTimes; //游戏局数
    private int count;//人数
    private boolean dianpao;//点炮
    private Integer[] dice;//骰子
    private List<Record> recordList = new ArrayList<>();//战绩
    private int gameCount;
    private int roomOwner;
    private int zhuangxian;

    private Integer bao;
    private Integer jiabao;
    private Date startDate;
    private int continuityBanker;
    private boolean qianShao;

    public int getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Integer> getSeatNos() {
        return seatNos;
    }

    public void setSeatNos(List<Integer> seatNos) {
        this.seatNos = seatNos;
    }

    public int getOperationSeatNo() {
        return operationSeatNo;
    }

    public void setOperationSeatNo(int operationSeatNo) {
        this.operationSeatNo = operationSeatNo;
    }

    public List<OperationHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<OperationHistory> historyList) {
        this.historyList = historyList;
    }

    public List<Integer> getSurplusCards() {
        return surplusCards;
    }

    public void setSurplusCards(List<Integer> surplusCards) {
        this.surplusCards = surplusCards;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getLastOperation() {
        return lastOperation;
    }

    public void setLastOperation(int lastOperation) {
        this.lastOperation = lastOperation;
    }

    public int getBanker() {
        return banker;
    }

    public void setBanker(int banker) {
        this.banker = banker;
    }

    public int getGameTimes() {
        return gameTimes;
    }

    public void setGameTimes(int gameTimes) {
        this.gameTimes = gameTimes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isDianpao() {
        return dianpao;
    }

    public void setDianpao(boolean dianpao) {
        this.dianpao = dianpao;
    }

    public Integer[] getDice() {
        return dice;
    }

    public void setDice(Integer[] dice) {
        this.dice = dice;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public int getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(int roomOwner) {
        this.roomOwner = roomOwner;
    }

    public int getZhuangxian() {
        return zhuangxian;
    }

    public void setZhuangxian(int zhuangxian) {
        this.zhuangxian = zhuangxian;
    }

    public Integer getBao() {
        return bao;
    }

    public void setBao(Integer bao) {
        this.bao = bao;
    }

    public Integer getJiabao() {
        return jiabao;
    }

    public void setJiabao(Integer jiabao) {
        this.jiabao = jiabao;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getContinuityBanker() {
        return continuityBanker;
    }

    public void setContinuityBanker(int continuityBanker) {
        this.continuityBanker = continuityBanker;
    }

    public boolean isQianShao() {
        return qianShao;
    }

    public void setQianShao(boolean qianShao) {
        this.qianShao = qianShao;
    }

    public void addSeat(User user, int score) {
        Seat seat = new Seat();
        seat.setRobot(false);
        seat.setReady(false);
        seat.setAreaString(user.getArea());
        seat.setHead(user.getHead());
        seat.setNickname(user.getNickname());
        seat.setSex(user.getSex().equals("1"));
        seat.setScore(score);
        seat.setSeatNo(seatNos.get(0));
        seat.setIp(user.getLastLoginIp());
        seat.setGameCount(user.getGameCount());
        seatNos.remove(0);
        seat.setUserId(user.getUserId());
        seats.add(seat);
    }

    public void dealCard() {
        startDate = new Date();
        surplusCards = Card.getAllCard();
        //卖马 发牌
        for (Seat seat : seats) {
            seat.setReady(false);
            List<Integer> cardList = new ArrayList<>();
            for (int i = 0; i < 13; i++) {
                int cardIndex = (int) (Math.random() * surplusCards.size());
                cardList.add(surplusCards.get(cardIndex));
                surplusCards.remove(cardIndex);
            }
            if (seat.getUserId() == banker) {
                operationSeatNo = seat.getSeatNo();
                int cardIndex = (int) (Math.random() * surplusCards.size());
                jiabao = surplusCards.get(cardIndex);
                surplusCards.remove(cardIndex);

                cardIndex = (int) (Math.random() * surplusCards.size());
                cardList.add(surplusCards.get(cardIndex));
                surplusCards.remove(cardIndex);
            }

            seat.setCards(cardList);
            seat.setInitialCards(cardList);
        }
//        int cardIndex = (int) (Math.random() * surplusCards.size());
//        jiabao = surplusCards.get(cardIndex);
//        surplusCards.remove(cardIndex);

        switch (jiabao / 10) {
            case 0:
            case 1:
            case 2:
                if (9 == jiabao % 10) {
                    bao = (jiabao / 10 * 10) + 1;
                } else {
                    bao = jiabao + 1;
                }
                break;
            case 3:
                if (35 == jiabao) {
                    bao = 31;
                } else {
                    bao = jiabao + 2;
                }
                break;
            case 4:
                if (47 == jiabao) {
                    bao = 41;
                } else {
                    bao = jiabao + 2;
                }
                break;
            case 5:
                bao = 51;
        }
    }

    public int getNextSeat() {
        int next = operationSeatNo;
        if (count == next) {
            next = 1;
        } else {
            next += 1;
        }
        return next;
    }

    public int getSeat(int seat, int count) {
        seat += count;
        if (seat > this.count) {
            seat = seat % this.count;
        }
        return seat;
    }

    private void clear(Map<Integer, Integer> huCard) {
        Record record = new Record();
        record.setJiabao(jiabao);
        record.setDice(dice);
        record.setBanker(banker);
        record.setStartDate(startDate);
        record.setGameCount(gameCount);
        List<SeatRecord> seatRecords = new ArrayList<>();
        seats.forEach(seat -> {
            SeatRecord seatRecord = new SeatRecord();
            seatRecord.setUserId(seat.getUserId());
            seatRecord.setNickname(seat.getNickname());
            seatRecord.setHead(seat.getHead());
            seatRecord.setCardResult(seat.getCardResult());
            seatRecord.getMingGangResult().addAll(seat.getMingGangResult());
            seatRecord.getAnGangResult().addAll(seat.getAnGangResult());
            seatRecord.getInitialCards().addAll(seat.getInitialCards());
            seatRecord.getCards().addAll(seat.getCards());
            seatRecord.getPengCards().addAll(seat.getPengCards());
            seatRecord.getChiCards().addAll(seat.getChiCards());
            seatRecord.getAnGangCards().addAll(seat.getAnGangCards());
            seatRecord.getMingGangCards().addAll(seat.getMingGangCards());
            seatRecord.setScore(seat.getScore());
            seatRecord.setSex(seat.isSex());
            seatRecord.setIp(seat.getIp());
            seatRecord.setSeatNo(seat.getSeatNo());
            seatRecord.setGameCount(gameCount);
            if (null != huCard && huCard.containsKey(seatRecord.getUserId())) {
                seatRecord.setHuCard(huCard.get(seatRecord.getUserId()));
            }
            final int[] winOrLose = {0};
            seat.getMingGangResult().forEach(gameResult -> winOrLose[0] += gameResult.getScore());
            seat.getAnGangResult().forEach(gameResult -> winOrLose[0] += gameResult.getScore());
            if (null != seat.getCardResult()) {
                winOrLose[0] += seat.getCardResult().getScore();
            }
            seatRecord.setWinOrLose(winOrLose[0]);
            seatRecords.add(seatRecord);
        });
        record.setSeatRecordList(seatRecords);
        record.getHistoryList().addAll(historyList);
        recordList.add(record);

        historyList.clear();
        surplusCards.clear();
        gameStatus = GameStatus.READYING;
        lastOperation = 0;
        dice = null;
        seats.forEach(Seat::clear);
        startDate = new Date();
        qianShao = false;
    }

    public void getCard(GameBase.BaseConnection.Builder response, int seatNo, RedisService redisService) {
        if (0 == surplusCards.size()) {
            gameOver(response, redisService, 0);
            return;
        }
        GameBase.BaseAction.Builder actionResponse = GameBase.BaseAction.newBuilder().setOperationId(GameBase.ActionId.GET_CARD);
        operationSeatNo = seatNo;
        int cardIndex = (int) (Math.random() * surplusCards.size());
        Integer card1 = surplusCards.get(cardIndex);
        surplusCards.remove(cardIndex);
        final Integer[] username = new Integer[1];
        seats.stream().filter(seat -> seat.getSeatNo() == seatNo).forEach(seat -> username[0] = seat.getUserId());
        actionResponse.setID(username[0]);

        historyList.add(new OperationHistory(username[0], OperationHistoryType.GET_CARD, card1));
        Mahjong.CardsData.Builder builder1 = Mahjong.CardsData.newBuilder();
        builder1.addCards(card1);

        Seat operationSeat = null;
        for (Seat seat : seats) {
            if (seat.getSeatNo() == seatNo) {
                seat.getCards().add(card1);
                seat.setCanNotHu(false);
                operationSeat = seat;
                actionResponse.setData(builder1.build().toByteString());
            } else {
                actionResponse.clearData();
            }
            if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
            }
        }

        GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder()
                .setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0).setID(username[0]).build();
        response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());
        seats.stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId()))
                .forEach(seat -> MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId()));

        checkSelfGetCard(response, operationSeat, redisService);
    }

    /**
     * 游戏结束
     *
     * @param response
     * @param redisService
     */
    public void gameOver(GameBase.BaseConnection.Builder response, RedisService redisService, int card) {
        Map<Integer, Integer> huCard = new HashMap<>();
        List<Integer> loseSeats = new ArrayList<>();
        final int[] score = {0};

        Mahjong.MahjongResultResponse.Builder resultResponse = Mahjong.MahjongResultResponse.newBuilder();
        resultResponse.setReadyTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0);
        seats.forEach(seat -> {
            Mahjong.MahjongUserResult.Builder userResult = Mahjong.MahjongUserResult.newBuilder();
            userResult.setID(seat.getUserId());
            userResult.addAllCards(seat.getCards());
            userResult.addAllChiCards(seat.getChiCards());
            userResult.addAllPengCards(seat.getPengCards());
            userResult.addAllAnGangCards(seat.getAnGangCards());
            userResult.addAllMingGangCards(seat.getMingGangCards());
            final int[] win = {0};
            if (null != seat.getCardResult()) {
                seat.getCardResult().setScore(seat.getCardResult().getScore() * baseScore);
                userResult.setCardScore(seat.getCardResult().getScore());
                win[0] += seat.getCardResult().getScore();
                if (seat.getCardResult().getScore() > 0) {
                    userResult.setHuCard(card);
                    userResult.setCardScore(seat.getCardResult().getScore());
                    huCard.put(seat.getUserId(), card);
                } else {
                    loseSeats.add(seat.getSeatNo());
                    score[0] = seat.getCardResult().getScore();
                }
                for (ScoreType scoreType : seat.getCardResult().getScoreTypes()) {
                    userResult.addScoreTypes(Mahjong.ScoreType.forNumber(scoreType.ordinal() - 3));
                }
            }
            int mingGangScore = 0;
            for (GameResult gameResult : seat.getMingGangResult()) {
                mingGangScore += gameResult.getScore();
            }
            userResult.setMingGangScore(mingGangScore);
            win[0] += mingGangScore;
            int anGangScore = 0;
            for (GameResult gameResult : seat.getAnGangResult()) {
                anGangScore += gameResult.getScore();
            }
            userResult.setAnGangScore(anGangScore);
            win[0] += anGangScore;

            if (qianShao) {
                if (seat.getUserId() == banker) {
                    win[0] -= (count - 1) * baseScore;
                } else {
                    win[0] += baseScore;
                }
            }

            userResult.setWinOrLose(win[0]);
            seat.setScore(seat.getScore() + win[0]);
            userResult.setScore(seat.getScore());
            resultResponse.addUserResult(userResult);
        });

        if (redisService.exists("room_match" + roomNo)) {
            GameBase.ScoreResponse.Builder scoreResponse = GameBase.ScoreResponse.newBuilder();
            for (Mahjong.MahjongUserResult.Builder userResult : resultResponse.getUserResultBuilderList()) {
                if (MahjongTcpService.userClients.containsKey(userResult.getID())) {
                    int win = userResult.getCardScore() + userResult.getMingGangScore() + userResult.getAnGangScore() + userResult.getMaScore();
                    GameBase.MatchResult matchResult;
                    if (gameCount != gameTimes) {
                        matchResult = GameBase.MatchResult.newBuilder().setResult(0).setCurrentScore(win)
                                .setTotalScore(userResult.getScore()).build();
                    } else {
                        matchResult = GameBase.MatchResult.newBuilder().setResult(2).setCurrentScore(win)
                                .setTotalScore(userResult.getScore()).build();
                    }
                    MahjongTcpService.userClients.get(userResult.getID()).send(response.setOperationType(GameBase.OperationType.MATCH_RESULT)
                            .setData(matchResult.toByteString()).build(), userResult.getID());
                }
                scoreResponse.addScoreResult(GameBase.ScoreResult.newBuilder().setID(userResult.getID()).setScore(userResult.getScore()));
            }
            for (Seat seat : seats) {
                if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                    MahjongTcpService.userClients.get(seat.getUserId()).send(response.setOperationType(GameBase.OperationType.MATCH_SCORE)
                            .setData(scoreResponse.build().toByteString()).build(), seat.getUserId());
                }
            }
        } else {
            response.setOperationType(GameBase.OperationType.RESULT).setData(resultResponse.build().toByteString());
            seats.stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId()))
                    .forEach(seat -> MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId()));
        }

        clear(huCard);
        //结束房间
        if (gameCount == gameTimes) {
            roomOver(response, redisService);
        } else {
            if (redisService.exists("room_match" + roomNo)) {
                new ReadyTimeout(Integer.valueOf(roomNo), redisService, gameCount).start();
            }
        }
    }

    public void roomOver(GameBase.BaseConnection.Builder response, RedisService redisService) {
        JSONObject jsonObject = new JSONObject();
        //是否竞技场
        if (redisService.exists("room_match" + roomNo)) {
            String matchNo = redisService.getCache("room_match" + roomNo);
            redisService.delete("room_match" + roomNo);
            if (redisService.exists("match_info" + matchNo)) {
                while (!redisService.lock("lock_match_info" + matchNo)) {
                }
                GameBase.MatchResult.Builder matchResult = GameBase.MatchResult.newBuilder();
                MatchInfo matchInfo = JSON.parseObject(redisService.getCache("match_info" + matchNo), MatchInfo.class);
                Arena arena = matchInfo.getArena();

                //移出当前桌
                List<Integer> rooms = matchInfo.getRooms();
                for (Integer integer : rooms) {
                    if (integer == Integer.parseInt(roomNo)) {
                        rooms.remove(integer);
                        break;
                    }
                }

                //等待的人
                List<MatchUser> waitUsers = matchInfo.getWaitUsers();
                if (null == waitUsers) {
                    waitUsers = new ArrayList<>();
                    matchInfo.setWaitUsers(waitUsers);
                }
                //在比赛中的人 重置分数
                List<MatchUser> matchUsers = matchInfo.getMatchUsers();
                for (Seat seat : seats) {
                    redisService.delete("reconnect" + seat.getUserId());
                    for (MatchUser matchUser : matchUsers) {
                        if (seat.getUserId() == matchUser.getUserId()) {
                            matchUser.setScore(seat.getScore());
                        }
                    }
//                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
//                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.setOperationType(GameBase.OperationType.ROOM_INFO).clearData().build(), seat.getUserId());
//                        GameBase.RoomSeatsInfo.Builder roomSeatsInfo = GameBase.RoomSeatsInfo.newBuilder();
//                        GameBase.SeatResponse.Builder seatResponse = GameBase.SeatResponse.newBuilder();
//                        seatResponse.setSeatNo(1);
//                        seatResponse.setID(seat.getUserId());
//                        seatResponse.setScore(seat.getScore());
//                        seatResponse.setReady(false);
//                        seatResponse.setIp(seat.getIp());
//                        seatResponse.setGameCount(seat.getGameCount());
//                        seatResponse.setNickname(seat.getNickname());
//                        seatResponse.setHead(seat.getHead());
//                        seatResponse.setSex(seat.isSex());
//                        seatResponse.setOffline(false);
//                        seatResponse.setIsRobot(seat.isRobot());
//                        roomSeatsInfo.addSeats(seatResponse.build());
//                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.setOperationType(GameBase.OperationType.SEAT_INFO).setData(roomSeatsInfo.build().toByteString()).build(), seat.getUserId());
//                    }
                }

                //用户对应分数
                Map<Integer, Integer> userIdScore = new HashMap<>();
                for (MatchUser matchUser : matchUsers) {
                    userIdScore.put(matchUser.getUserId(), matchUser.getScore());
                }

                GameBase.MatchData.Builder matchData = GameBase.MatchData.newBuilder();
                switch (matchInfo.getStatus()) {
                    case 1:
                        //TODO 少一个0，记得加回来

                        //根据金币排序
                        seats.sort(new Comparator<Seat>() {
                            @Override
                            public int compare(Seat o1, Seat o2) {
                                return o1.getScore() > o2.getScore() ? 1 : -1;
                            }
                        });

                        //本局未被淘汰的
                        List<MatchUser> thisWait = new ArrayList<>();
                        //循环座位，淘汰
                        for (Seat seat : seats) {
                            for (MatchUser matchUser : matchUsers) {
                                if (matchUser.getUserId() == seat.getUserId()) {
                                    if (seat.getScore() < matchInfo.getMatchEliminateScore() && matchUsers.size() > arena.getCount() / 2) {
                                        matchUsers.remove(matchUser);

                                        matchResult.setResult(3).setTotalScore(seat.getScore()).setCurrentScore(-1);
                                        response.setOperationType(GameBase.OperationType.MATCH_RESULT).setData(matchResult.build().toByteString());
                                        if (MahjongTcpService.userClients.containsKey(matchUser.getUserId())) {
                                            MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                                        }
                                        response.setOperationType(GameBase.OperationType.MATCH_BALANCE).setData(GameBase.MatchBalance.newBuilder()
                                                .setRanking(matchUsers.size()).setTotalScore(matchUser.getScore()).build().toByteString());
                                        if (MahjongTcpService.userClients.containsKey(matchUser.getUserId())) {
                                            MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                                            GameBase.OverResponse.Builder over = GameBase.OverResponse.newBuilder();
                                            String uuid = UUID.randomUUID().toString().replace("-", "");
                                            while (redisService.exists(uuid)) {
                                                uuid = UUID.randomUUID().toString().replace("-", "");
                                            }
                                            redisService.addCache("backkey" + uuid, seat.getUserId() + "", 1800);
                                            over.setBackKey(uuid);
                                            over.setDateTime(new Date().getTime());
                                            response.setOperationType(GameBase.OperationType.OVER).setData(over.build().toByteString());
                                            MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                                        }

                                        redisService.delete("reconnect" + seat.getUserId());
                                    } else {
                                        thisWait.add(matchUser);
                                        redisService.addCache("reconnect" + seat.getUserId(), "ruijin_mahjong," + matchNo);
                                    }
                                    break;
                                }
                            }
                        }

                        //淘汰人数以满
                        int count = matchUsers.size();
                        if (count == arena.getCount() / 2 && 0 == rooms.size()) {
                            waitUsers.clear();
                            List<User> users = new ArrayList<>();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (MatchUser matchUser : matchUsers) {
                                stringBuilder.append(",").append(matchUser.getUserId());
                            }
                            jsonObject.clear();
                            jsonObject.put("userIds", stringBuilder.toString().substring(1));
                            ApiResponse<List<User>> usersResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.userListUrl, jsonObject.toJSONString()),
                                    new TypeReference<ApiResponse<List<User>>>() {
                                    });
                            if (0 == usersResponse.getCode()) {
                                users = usersResponse.getData();
                            }

                            //第二轮开始
                            matchInfo.setStatus(2);
                            matchData.setStatus(2);
                            matchData.setCurrentCount(matchUsers.size());
                            matchData.setRound(1);
                            while (4 <= users.size()) {
                                rooms.add(matchInfo.addRoom(matchNo, 2, redisService, users.subList(0, 4), userIdScore, response, matchData));
                            }
                        } else if (count > arena.getCount() / 2) {
                            //满四人继续匹配
                            waitUsers.addAll(thisWait);
                            while (4 <= waitUsers.size()) {
                                //剩余用户
                                List<User> users = new ArrayList<>();
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < 4; i++) {
                                    stringBuilder.append(",").append(waitUsers.remove(0).getUserId());
                                }
                                jsonObject.clear();
                                jsonObject.put("userIds", stringBuilder.toString().substring(1));
                                ApiResponse<List<User>> usersResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.userListUrl, jsonObject.toJSONString()),
                                        new TypeReference<ApiResponse<List<User>>>() {
                                        });
                                if (0 == usersResponse.getCode()) {
                                    users = usersResponse.getData();
                                }
                                matchData.setStatus(1);
                                matchData.setCurrentCount(matchUsers.size());
                                matchData.setRound(1);
                                rooms.add(matchInfo.addRoom(matchNo, 1, redisService, users, userIdScore, response, matchData));
                            }
                        }
                        break;
                    case 2:
                    case 3:
                        for (Seat seat : seats) {
                            redisService.addCache("reconnect" + seat.getUserId(), "ruijin_mahjong," + matchNo);
                        }
                        if (0 == rooms.size()) {
                            matchInfo.setStatus(matchInfo.getStatus() + 1);
                            matchData.setStatus(2);

                            List<User> users = new ArrayList<>();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (MatchUser matchUser : matchUsers) {
                                stringBuilder.append(",").append(matchUser.getUserId());
                            }
                            jsonObject.clear();
                            jsonObject.put("userIds", stringBuilder.toString().substring(1));
                            ApiResponse<List<User>> usersResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.userListUrl, jsonObject.toJSONString()),
                                    new TypeReference<ApiResponse<List<User>>>() {
                                    });
                            if (0 == usersResponse.getCode()) {
                                users = usersResponse.getData();
                            }
                            matchData.setCurrentCount(matchUsers.size());
                            matchData.setRound(matchInfo.getStatus() - 1);
                            while (4 <= users.size()) {
                                rooms.add(matchInfo.addRoom(matchNo, 2, redisService, users.subList(0, 4), userIdScore, response, matchData));
                            }
                        }
                        break;
                    case 4:
                        for (Seat seat : seats) {
                            MatchUser matchUser = new MatchUser();
                            matchUser.setUserId(seat.getUserId());
                            matchUser.setScore(seat.getScore());
                            waitUsers.add(matchUser);
                            redisService.addCache("reconnect" + seat.getUserId(), "ruijin_mahjong," + matchNo);
                        }

                        waitUsers.sort(new Comparator<MatchUser>() {
                            @Override
                            public int compare(MatchUser o1, MatchUser o2) {
                                return o1.getScore() > o2.getScore() ? -1 : 1;
                            }
                        });
                        while (waitUsers.size() > 4) {
                            MatchUser matchUser = waitUsers.remove(waitUsers.size() - 1);

                            response.setOperationType(GameBase.OperationType.MATCH_BALANCE).setData(GameBase.MatchBalance.newBuilder()
                                    .setRanking(matchUsers.size()).setTotalScore(matchUser.getScore()).build().toByteString());
                            if (MahjongTcpService.userClients.containsKey(matchUser.getUserId())) {
                                MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                                GameBase.OverResponse.Builder over = GameBase.OverResponse.newBuilder();
                                String uuid = UUID.randomUUID().toString().replace("-", "");
                                while (redisService.exists(uuid)) {
                                    uuid = UUID.randomUUID().toString().replace("-", "");
                                }
                                redisService.addCache("backkey" + uuid, matchUser.getUserId() + "", 1800);
                                over.setBackKey(uuid);
                                over.setDateTime(new Date().getTime());
                                response.setOperationType(GameBase.OperationType.OVER).setData(over.build().toByteString());
                                MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                            }
                            redisService.delete("reconnect" + matchUser.getUserId());
                        }

                        if (0 == rooms.size()) {

                            matchUsers.clear();
                            matchUsers.addAll(waitUsers);
                            waitUsers.clear();

                            matchInfo.setStatus(5);
                            matchData.setStatus(3);

                            List<User> users = new ArrayList<>();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (MatchUser matchUser : matchUsers) {
                                stringBuilder.append(",").append(matchUser.getUserId());
                            }
                            jsonObject.clear();
                            jsonObject.put("userIds", stringBuilder.toString().substring(1));
                            ApiResponse<List<User>> usersResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.userListUrl, jsonObject.toJSONString()),
                                    new TypeReference<ApiResponse<List<User>>>() {
                                    });
                            if (0 == usersResponse.getCode()) {
                                users = usersResponse.getData();
                            }
                            matchData.setCurrentCount(matchUsers.size());
                            matchData.setRound(1);
                            while (4 == users.size()) {
                                rooms.add(matchInfo.addRoom(matchNo, 2, redisService, users, userIdScore, response, matchData));
                            }
                        }
                        break;
                    case 5:
                        matchUsers.sort(new Comparator<MatchUser>() {
                            @Override
                            public int compare(MatchUser o1, MatchUser o2) {
                                return o1.getScore() > o2.getScore() ? -1 : 1;
                            }
                        });
                        for (int i = 0; i < matchUsers.size(); i++) {
                            if (i == 0 && matchInfo.getArena().getArenaType() == 0) {
                                jsonObject.clear();
                                jsonObject.put("flowType", 1);
                                jsonObject.put("money", matchInfo.getArena().getReward());
                                jsonObject.put("description", "比赛获胜" + matchInfo.getArena().getId());
                                jsonObject.put("userId", matchUsers.get(i).getUserId());
                                ApiResponse moneyDetail = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.moneyDetailedCreate, jsonObject.toJSONString()), new TypeReference<ApiResponse<User>>() {
                                });
                                if (0 != moneyDetail.getCode()) {
                                    LoggerFactory.getLogger(this.getClass()).error(Constant.apiUrl + Constant.moneyDetailedCreate + "?" + jsonObject.toJSONString());
                                }
                            }
                            matchResult.setResult(i == 0 ? 1 : 3).setTotalScore(matchUsers.get(i).getScore()).setCurrentScore(-1);
                            response.setOperationType(GameBase.OperationType.MATCH_RESULT).setData(matchResult.build().toByteString());
                            if (MahjongTcpService.userClients.containsKey(matchUsers.get(i).getUserId())) {
                                MahjongTcpService.userClients.get(matchUsers.get(i).getUserId()).send(response.build(), matchUsers.get(i).getUserId());
                            }
                            response.setOperationType(GameBase.OperationType.MATCH_BALANCE).setData(GameBase.MatchBalance.newBuilder()
                                    .setRanking(i + 1).setTotalScore(matchUsers.get(i).getScore()).build().toByteString());
                            if (MahjongTcpService.userClients.containsKey(matchUsers.get(i).getUserId())) {
                                MahjongTcpService.userClients.get(matchUsers.get(i).getUserId()).send(response.build(), matchUsers.get(i).getUserId());
                                GameBase.OverResponse.Builder over = GameBase.OverResponse.newBuilder();
                                String uuid = UUID.randomUUID().toString().replace("-", "");
                                while (redisService.exists(uuid)) {
                                    uuid = UUID.randomUUID().toString().replace("-", "");
                                }
                                redisService.addCache("backkey" + uuid, matchUsers.get(i).getUserId() + "", 1800);
                                over.setBackKey(uuid);
                                over.setDateTime(new Date().getTime());
                                response.setOperationType(GameBase.OperationType.OVER).setData(over.build().toByteString());
                                MahjongTcpService.userClients.get(matchUsers.get(i).getUserId()).send(response.build(), matchUsers.get(i).getUserId());
                            }
                        }
                        matchInfo.setStatus(-1);
                        break;
                }
                if (0 < matchInfo.getStatus()) {
                    matchInfo.setMatchUsers(matchUsers);
                    matchInfo.setRooms(rooms);
                    matchInfo.setWaitUsers(waitUsers);
                    redisService.addCache("match_info" + matchNo, JSON.toJSONString(matchInfo));
                }
                redisService.unlock("lock_match_info" + matchNo);
            }
        } else {
            if (0 == gameStatus.compareTo(GameStatus.WAITING)) {
                jsonObject.clear();
                jsonObject.put("flowType", 1);
                switch (gameTimes) {
                    case 4:
                        jsonObject.put("money", 1);
                        break;
                    case 8:
                        jsonObject.put("money", 2);
                        break;
                    case 16:
                        jsonObject.put("money", 3);
                        break;
                }
                jsonObject.put("description", "开房间退回" + roomNo);
                jsonObject.put("userId", roomOwner);
                ApiResponse moneyDetail = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.moneyDetailedCreate, jsonObject.toJSONString()), new TypeReference<ApiResponse<User>>() {
                });
                if (0 != moneyDetail.getCode()) {
                    LoggerFactory.getLogger(this.getClass()).error(Constant.apiUrl + Constant.moneyDetailedCreate + "?" + jsonObject.toJSONString());
                }
            }
            if (0 != recordList.size()) {
                Mahjong.MahjongBalanceResponse.Builder balance = Mahjong.MahjongBalanceResponse.newBuilder();
                for (Seat seat : seats) {
                    Mahjong.MahjongSeatGameBalance.Builder seatGameBalance = Mahjong.MahjongSeatGameBalance.newBuilder()
                            .setID(seat.getUserId()).setMinggang(seat.getMinggang()).setAngang(seat.getAngang())
                            .setZimoCount(seat.getZimoCount()).setHuCount(seat.getHuCount())
                            .setDianpaoCount(seat.getDianpaoCount()).setWinOrLose(seat.getScore());
                    balance.addGameBalance(seatGameBalance);
                }

                for (Seat seat : seats) {
                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                        response.setOperationType(GameBase.OperationType.BALANCE).setData(balance.build().toByteString());
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                    }
                }
            }
            StringBuilder people = new StringBuilder();

            GameBase.OverResponse.Builder over = GameBase.OverResponse.newBuilder();
            for (Seat seat : seats) {
                people.append(",").append(seat.getUserId());
                redisService.delete("reconnect" + seat.getUserId());
                if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    while (redisService.exists(uuid)) {
                        uuid = UUID.randomUUID().toString().replace("-", "");
                    }
                    redisService.addCache("backkey" + uuid, seat.getUserId() + "", 1800);
                    over.setBackKey(uuid);
                    over.setDateTime(new Date().getTime());
                    response.setOperationType(GameBase.OperationType.OVER).setData(over.build().toByteString());
                    MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                }
            }

            if (0 != recordList.size()) {
                List<TotalScore> totalScores = new ArrayList<>();
                for (Seat seat : seats) {
                    TotalScore totalScore = new TotalScore();
                    totalScore.setHead(seat.getHead());
                    totalScore.setNickname(seat.getNickname());
                    totalScore.setUserId(seat.getUserId());
                    totalScore.setScore(seat.getScore());
                    totalScores.add(totalScore);
                }
                SerializerFeature[] features = new SerializerFeature[]{SerializerFeature.WriteNullListAsEmpty,
                        SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect,
                        SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero,
                        SerializerFeature.WriteNullBooleanAsFalse};
                int feature = SerializerFeature.config(JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.WriteEnumUsingName, false);
                jsonObject.clear();
                jsonObject.put("gameType", 1);
                jsonObject.put("roomOwner", roomOwner);
                jsonObject.put("people", people.toString().substring(1));
                jsonObject.put("gameTotal", gameTimes);
                jsonObject.put("gameCount", gameCount);
                jsonObject.put("peopleCount", count);
                jsonObject.put("roomNo", Integer.parseInt(roomNo));
                JSONObject gameRule = new JSONObject();
                gameRule.put("zhuangxian", zhuangxian);
                gameRule.put("dianpao", dianpao);
                gameRule.put("baseScore", baseScore);
                gameRule.put("rogue", jiabao);
                jsonObject.put("gameRule", gameRule.toJSONString());
                jsonObject.put("gameData", JSON.toJSONString(recordList, feature, features).getBytes());
                jsonObject.put("scoreData", JSON.toJSONString(totalScores, feature, features).getBytes());

                ApiResponse apiResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.gamerecordCreateUrl, jsonObject.toJSONString()), ApiResponse.class);
                if (0 != apiResponse.getCode()) {
                    LoggerFactory.getLogger(this.getClass()).error(Constant.apiUrl + Constant.gamerecordCreateUrl + "?" + jsonObject.toJSONString());
                }
            }
        }

        //删除该桌
        redisService.delete("room" + roomNo);
        redisService.delete("room_type" + roomNo);
        roomNo = null;
    }

    /**
     * 摸牌后检测是否可以自摸、暗杠、扒杠
     *
     * @param seat 座位
     */
    public void checkSelfGetCard(GameBase.BaseConnection.Builder response, Seat seat, RedisService redisService) {
        GameBase.AskResponse.Builder builder = GameBase.AskResponse.newBuilder();
        builder.setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0);
//        if (MahjongUtil.hu(seat.getCards(), bao)) {
        if (MahjongUtil.fei(seat.getCards(), bao) || MahjongUtil.hu(seat.getCards(), bao)) {
            builder.addOperationId(GameBase.ActionId.HU);
            if (redisService.exists("room_match" + roomNo)) {
                new OperationTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService, true).start();
            }
        }
        //暗杠
        if (null != MahjongUtil.checkGang(seat.getCards(), bao) && 0 < surplusCards.size()) {
            builder.addOperationId(GameBase.ActionId.AN_GANG);
        }
        //扒杠
        if (null != MahjongUtil.checkBaGang(seat.getCards(), seat.getPengCards()) && 0 < surplusCards.size()) {
            builder.addOperationId(GameBase.ActionId.BA_GANG);
        }
        if (0 != builder.getOperationIdCount()) {
            if (redisService.exists("room_match" + roomNo) && !builder.getOperationIdList().contains(GameBase.ActionId.HU)) {
                new OperationTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService, false).start();
            }
            if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                response.clear();
                response.setOperationType(GameBase.OperationType.ASK).setData(builder.build().toByteString());
                MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
            }
        } else {
            if (redisService.exists("room_match" + roomNo)) {
                new PlayCardTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService).start();
            }
        }
    }

    /**
     * 和牌
     */
    public void hu(int userId, GameBase.BaseConnection.Builder response, RedisService redisService) {
        //和牌的人
        final Seat[] huSeat = new Seat[1];
        seats.stream().filter(seat -> seat.getUserId() == userId)
                .forEach(seat -> huSeat[0] = seat);
        List<ScoreType> scoreTypes = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        for (Integer card1 : huSeat[0].getCards()) {
            temp.add(card1 > 50 ? bao : card1);
        }
        //检查是自摸还是点炮,自摸输家是其它三家
//        if (MahjongUtil.fei(huSeat[0].getCards(), bao) || MahjongUtil.hu(huSeat[0].getCards(), bao)) {

        ScoreType scoreType = null;
        if (MahjongUtil.fei(huSeat[0].getCards(), bao)) {
            scoreType = ScoreType.FEI;
        } else if (MahjongUtil.hu(huSeat[0].getCards(), bao)) {
            scoreType = ScoreType.ZIMO_HU;
        }

        if (null != scoreType) {
            if (0 < historyList.size()) {
                if (0 != historyList.get(historyList.size() - 1).getHistoryType().compareTo(OperationHistoryType.GET_CARD)
                        || historyList.get(historyList.size() - 1).getUserId() != userId) {
                    return;
                }
            }

            int score = MahjongUtil.getScore(scoreType);

            //天胡
            if (historyList.size() == 0 && score < 10) {
                scoreType = ScoreType.TIAN_HU;
                score = 10;
            }
            if (banker == huSeat[0].getUserId()) {
                if (1 == zhuangxian) {
                    score += 1;
                } else {
                    score += continuityBanker;
                }
            }

            int loseScore[] = {0};
            scoreTypes.add(scoreType);
            int finalScore = score;
            seats.stream().filter(seat -> seat.getUserId() != userId).forEach(seat -> {
                if (banker == seat.getUserId()) {
                    int score1 = -finalScore - 1;
                    if (1 == zhuangxian) {
                        if (score1 < -10) {
                            score1 = -10;
                        }
                        seat.setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), score1));
                        loseScore[0] += score1;
                    } else {
                        seat.setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), -finalScore - continuityBanker));
                        loseScore[0] += -finalScore - continuityBanker;
                    }
                } else {
                    int score1 = -finalScore;
                    if (score1 < -10) {
                        score1 = -10;
                    }
                    seat.setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), score1));
                    loseScore[0] += score1;
                }
            });
            if (huSeat[0].getUserId() == banker) {
                continuityBanker++;
            } else {
                continuityBanker = 0;
                banker = huSeat[0].getUserId();
            }
            huSeat[0].setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), -loseScore[0]));
            huSeat[0].setZimoCount(huSeat[0].getZimoCount() + 1);

            Mahjong.MahjongHuResponse.Builder mahjongHuResponse = Mahjong.MahjongHuResponse.newBuilder().addCards(huSeat[0].getCards().size() - 1);
            mahjongHuResponse.addScoreType(Mahjong.ScoreType.forNumber(scoreType.ordinal() - 3));

            response.setOperationType(GameBase.OperationType.ACTION).setData(GameBase.BaseAction.newBuilder().setOperationId(GameBase.ActionId.HU)
                    .setID(huSeat[0].getUserId()).setData(mahjongHuResponse.build().toByteString()).build().toByteString());
            seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                    .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
            gameOver(response, redisService, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1));

            return;
        }

        if (dianpao) {
            //找到那张牌
            int card = 0;
            Seat loseSeat = null;
            for (Seat seat : seats) {
                if (seat.getSeatNo() == operationSeatNo) {
                    card = seat.getPlayedCards().get(seat.getPlayedCards().size() - 1);
                    loseSeat = seat;
                    break;
                }
            }

            boolean hu = false;
            for (Seat seat : seats) {
                if (seat.getSeatNo() != operationSeatNo) {
                    temp.clear();
                    for (Integer card1 : seat.getCards()) {
                        temp.add(card1 > 50 ? bao : card1);
                    }
                    //当前玩家是否可以胡牌
                    temp.add(card > 50 ? bao : card);
                    List<Seat> huShun = new ArrayList<>();
                    for (Seat seat1 : seats) {
                        if (seat1.getSeatNo() > loseSeat.getSeatNo() && seat1.getOperation() == 0) {
                            huShun.add(seat1);
                        }
                    }
                    for (Seat seat1 : seats) {
                        if (seat1.getSeatNo() < loseSeat.getSeatNo() && seat1.getOperation() == 0) {
                            huShun.add(seat1);
                        }
                    }
                    boolean canHU = true;
                    for (Seat seat1 : huShun) {
                        if (MahjongUtil.hu(seat1.getCards(), bao) && seat1.getUserId() != userId) {
                            canHU = false;
                            break;
                        }
                    }

                    if (MahjongUtil.hu(temp, bao) && canHU) {
                        scoreType = ScoreType.PING_HU;
                        int score = MahjongUtil.getScore(scoreType);
                        //地胡
                        if (historyList.size() == 1 && score < 20) {
                            scoreType = ScoreType.DI_HU;
                            score = 10;
                        }
                        scoreTypes.add(scoreType);

                        int loseScore[] = {0};
                        Seat finalLoseSeat = loseSeat;

                        if (banker == seat.getUserId()) {
                            continuityBanker++;
                            if (1 == zhuangxian) {
                                score += 1;
                            }
                        } else {
                            continuityBanker = 0;
                        }

                        int finalScore = score;
                        ScoreType finalScoreType = scoreType;
                        seats.stream().filter(seat1 -> seat1.getUserId() != seat.getUserId()).forEach(seat1 -> {
                            if (seat1.getUserId() == finalLoseSeat.getUserId()) {
                                int score1 = finalScore;
                                if (finalScoreType == ScoreType.DI_HU) {
                                    score1 = finalScore - 1;
                                }
                                if (seat1.getUserId() == banker && 1 == zhuangxian) {
                                    seat1.setCardResult(new GameResult(scoreTypes, seat1.getCards().get(seat1.getCards().size() - 1), (-score1 - 2) < -10 ? -10 : (-score1 - 2)));
                                    loseScore[0] += (-score1 - 2) < -10 ? -10 : (-score1 - 2);
                                } else {
                                    seat1.setCardResult(new GameResult(scoreTypes, seat1.getCards().get(seat1.getCards().size() - 1), (-score1 - 1) < -10 ? -10 : (-score1 - 1)));
                                    loseScore[0] += (-score1 - 1) < -10 ? -10 : (-score1 - 1);
                                }
                            } else {
                                if (seat1.getUserId() == banker && 1 == zhuangxian) {
                                    seat1.setCardResult(new GameResult(scoreTypes, seat1.getCards().get(seat1.getCards().size() - 1), (-finalScore - 1) < -10 ? -10 : (-finalScore - 1)));
                                    loseScore[0] += -finalScore - 1;
                                } else {
                                    seat1.setCardResult(new GameResult(scoreTypes, seat1.getCards().get(seat1.getCards().size() - 1), -finalScore < -10 ? -10 : -finalScore));
                                    loseScore[0] += -finalScore;
                                }
                            }
                        });

                        banker = huSeat[0].getUserId();

                        seat.setCardResult(new GameResult(scoreTypes, seat.getCards().get(seat.getCards().size() - 1), -loseScore[0]));
                        seat.setHuCount(seat.getHuCount() + 1);

                        Mahjong.MahjongHuResponse.Builder mahjongHuResponse = Mahjong.MahjongHuResponse.newBuilder().addCards(huSeat[0].getCards().size() - 1);
                        mahjongHuResponse.addScoreType(Mahjong.ScoreType.forNumber(scoreType.ordinal() - 3));

                        response.setOperationType(GameBase.OperationType.ACTION).setData(GameBase.BaseAction.newBuilder().setOperationId(GameBase.ActionId.HU)
                                .setID(seat.getUserId()).setData(mahjongHuResponse.build().toByteString()).build().toByteString());
                        seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                                .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));


                        hu = true;
                        break;
                    }
                }
            }
            if (hu) {
                loseSeat.setDianpaoCount(loseSeat.getDianpaoCount() + 1);
                //胡牌
                gameOver(response, redisService, card);
                return;
            }
        }

//        if (checkCanChi(huSeat[0].getSeatNo(), true)) {
//            fei(response, redisService);
//        }

    }

    /**
     * 暗杠或者扒杠
     *
     * @param actionResponse
     * @param card
     * @param response
     * @param redisService
     */
    public void selfGang(GameBase.BaseAction.Builder actionResponse, Integer card, GameBase.BaseConnection.Builder response, RedisService redisService, int userId) {
        //碰或者杠
        seats.stream().filter(seat -> seat.getSeatNo() == operationSeatNo).forEach(seat -> {
            if (4 == Card.containSize(seat.getCards(), card)) {//暗杠
                List<Integer> gangCards = new ArrayList<>();
                gangCards.add(Card.remove(seat.getCards(), card));
                gangCards.add(Card.remove(seat.getCards(), card));
                gangCards.add(Card.remove(seat.getCards(), card));
                gangCards.add(Card.remove(seat.getCards(), card));

                seat.getAnGangCards().addAll(gangCards);

                List<ScoreType> scoreTypes = new ArrayList<>();
                scoreTypes.add(ScoreType.AN_GANG);

                final int[] loseSize = {0};
                seats.stream().filter(seat1 -> seat1.getSeatNo() != seat.getSeatNo())
                        .forEach(seat1 -> {
                            seat1.getAnGangResult().add(new GameResult(scoreTypes, card, -(2 * baseScore)));
                            loseSize[0]++;
                        });
                seat.getAnGangResult().add(new GameResult(scoreTypes, card, (2 * baseScore) * loseSize[0]));
                seat.setAngang(seat.getAngang() + 1);
                historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.AN_GANG, card));

                if (card < 50) {
                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addCards(card).build().toByteString());
                } else {
                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addAllCards(gangCards).build().toByteString());
                }
                seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                        .forEach(seat1 -> {
                            if (seat1.getUserId() == seat.getUserId()) {
                                if (card < 50) {
                                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addCards(card).build().toByteString());
                                } else {
                                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addAllCards(gangCards).build().toByteString());
                                }
                                response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                            } else {
                                if (card < 50) {
                                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addCards(0).build().toByteString());
                                } else {
                                    actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.CardsData.newBuilder().addCards(0).addCards(0).addCards(0).addCards(0).build().toByteString());
                                }
                                response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                            }
                            MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId());
                        });
                getCard(response, seat.getSeatNo(), redisService);
            } else if (3 == Card.containSize(seat.getPengCards(), card) && 1 == Card.containSize(seat.getCards(), card)) {//扒杠
                List<Integer> gangList = new ArrayList<>();
                gangList.add(Card.remove(seat.getCards(), card));
                gangList.add(Card.remove(seat.getPengCards(), card));
                gangList.add(Card.remove(seat.getPengCards(), card));
                gangList.add(Card.remove(seat.getPengCards(), card));

                seat.getMingGangCards().addAll(gangList);

                List<ScoreType> scoreTypes = new ArrayList<>();
                scoreTypes.add(ScoreType.BA_GANG);

                final int[] loseSize = {0};
                seats.stream().filter(seat1 -> seat1.getSeatNo() != seat.getSeatNo())
                        .forEach(seat1 -> {
                            seat1.getMingGangResult().add(new GameResult(scoreTypes, card, -baseScore));
                            loseSize[0]++;
                        });
                seat.getMingGangResult().add(new GameResult(scoreTypes, card, loseSize[0] * baseScore));
                seat.setMinggang(seat.getMinggang() + 1);
                historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.BA_GANG, card));

                actionResponse.setOperationId(GameBase.ActionId.BA_GANG).setData(Mahjong.CardsData.newBuilder().addAllCards(gangList).build().toByteString());
                response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                        .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                getCard(response, seat.getSeatNo(), redisService);
            }
        });
    }

    /**
     * 出牌后检查是否有人能胡、杠、碰
     *
     * @param card         当前出的牌
     * @param response
     * @param redisService
     */

    public void checkCard(Integer card, GameBase.BaseConnection.Builder response, RedisService redisService) {
        seats.forEach(seat1 -> {
            seat1.setOperation(0);
            seat1.getChiTemp().clear();
        });
        GameBase.AskResponse.Builder builder = GameBase.AskResponse.newBuilder();
        builder.setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0);
        //先检查胡，胡优先
        final boolean[] cannotOperation = {false};
        if (card.intValue() != bao) {
            seats.stream().filter(seat -> seat.getSeatNo() != operationSeatNo).forEach(seat -> {
                builder.clearOperationId();
                List<Integer> temp = new ArrayList<>();
                temp.addAll(seat.getCards());

                //检测吃
                //同条万只能下家吃，否则都可以吃
//                if (30 > card || (card > 50 && bao < 30)) {
                //下家
                if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                    if (MahjongUtil.checkChi(seat.getCards(), card, bao)) {
                        builder.addOperationId(GameBase.ActionId.CHI);
                    }
                }
//                } else {
//                    if (MahjongUtil.checkChi(seat.getCards(), card, bao)) {
//                        builder.addOperationId(GameBase.ActionId.CHI);
//                    }
//                }
                //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
                int containSize = Card.containSize(temp, card);
                if (3 == containSize && 0 < surplusCards.size()) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                    if (0 < surplusCards.size()) {
                        builder.addOperationId(GameBase.ActionId.DIAN_GANG);
                    }
                } else if (2 == containSize) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                }
                //当前玩家是否可以胡牌
                temp.add(card);
//                if (dianpao && MahjongUtil.fei(temp, bao) || MahjongUtil.hu(temp, bao)) {
                if (dianpao && MahjongUtil.hu(temp, bao) && !seat.isCanNotHu()) {
                    builder.addOperationId(GameBase.ActionId.HU);
                    if (redisService.exists("room_match" + roomNo)) {
                        new OperationTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService, true).start();
                    }
                }
                if (0 != builder.getOperationIdCount()) {
                    if (redisService.exists("room_match" + roomNo) && !builder.getOperationIdList().contains(GameBase.ActionId.HU)) {
                        new OperationTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService, false).start();
                    }
                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                        response.setOperationType(GameBase.OperationType.ASK).setData(builder.build().toByteString());
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                    }
                    cannotOperation[0] = true;
                }
            });
        }
        if (!cannotOperation[0]) {
            //如果没有人可以胡、碰、杠，游戏继续，下家摸牌；
            getCard(response, getNextSeat(), redisService);
        }
    }

    /**
     * 重连时检查出牌后是否有人能胡、杠、碰
     *
     * @param card 当前出的牌
     */
    public void checkSeatCan(Integer card, GameBase.BaseConnection.Builder response, int userId, Date date, RedisService redisService) {
        GameBase.AskResponse.Builder builder = GameBase.AskResponse.newBuilder();
        int time = 0;
        if (redisService.exists("room_match" + roomNo)) {
            time = 8 - (int) ((new Date().getTime() - date.getTime()) / 1000);
        }
        builder.setTimeCounter(time > 0 ? time : 0);
        if (card.intValue() != bao) {
            //先检查胡，胡优先
            seats.stream().filter(seat -> seat.getUserId() == userId).forEach(seat -> {
                builder.clearOperationId();
                List<Integer> temp = new ArrayList<>();
                temp.addAll(seat.getCards());

                //检测吃
                //同条万只能下家吃，否则都可以吃
//                if (30 > card || (card > 50 && bao < 30)) {
                //下家
                if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                    if (MahjongUtil.checkChi(seat.getCards(), card, bao)) {
                        builder.addOperationId(GameBase.ActionId.CHI);
                    }
                }
//                } else {
//                    if (MahjongUtil.checkChi(seat.getCards(), card, bao)) {
//                        builder.addOperationId(GameBase.ActionId.CHI);
//                    }
//                }
                //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
                int containSize = Card.containSize(temp, card);
                if (3 == containSize && 0 < surplusCards.size()) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                    if (0 < surplusCards.size()) {
                        builder.addOperationId(GameBase.ActionId.DIAN_GANG);
                    }
                } else if (2 == containSize) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                }
                //当前玩家是否可以胡牌
                temp.add(card);
//                if (dianpao && MahjongUtil.fei(temp, bao) || MahjongUtil.hu(temp, bao)) {
                if (dianpao && MahjongUtil.hu(temp, bao) && !seat.isCanNotHu()) {
                    builder.addOperationId(GameBase.ActionId.HU);
                }
                if (0 != builder.getOperationIdCount()) {
                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                        response.setOperationType(GameBase.OperationType.ASK).setData(builder.build().toByteString());
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                    }
                }
            });
        }
    }

    /**
     * 当有人吃后，再次检查是否还有人胡、碰、杠
     */
    public boolean checkCanChi(int seatNo, boolean fei) {
        //找到那张牌
        final Integer[] card = new Integer[1];
        seats.stream().filter(seat -> seat.getSeatNo() == operationSeatNo)
                .forEach(seat -> card[0] = seat.getPlayedCards().get(seat.getPlayedCards().size() - 1));
        final boolean[] canOperation = {true};
        seats.stream().filter(seat -> seat.getSeatNo() != operationSeatNo).forEach(seat -> {
            List<Integer> temp = new ArrayList<>();
            temp.addAll(seat.getCards());
            //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
            int containSize = Card.containSize(temp, card[0]);
            if (3 == containSize && 0 < surplusCards.size() && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
                canOperation[0] = false;
                return;
            } else if (2 == containSize && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
                canOperation[0] = false;
                return;
            }
            //当前玩家是否可以胡牌
            temp.add(card[0]);
            temp.clear();
            for (Integer card1 : seat.getCards()) {
                temp.add(card1 > 50 ? bao : card1);
            }
            temp.add(card[0] > 50 ? bao : card[0]);
//            if ((MahjongUtil.hu(temp, bao) || MahjongUtil.fei(temp, bao) && dianpao) && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
            if ((MahjongUtil.hu(temp, bao) && dianpao) && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
                canOperation[0] = false;
                return;
            }
            if (0 == seatNo) {
                return;
            }
            if (seatBetween(seat.getSeatNo(), operationSeatNo, seatNo) && !fei) {
//                if (30 > card[0] || (card[0] > 50 && bao < 30)) {
                //下家
                if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                    if (MahjongUtil.checkChi(seat.getCards(), card[0], bao) && 4 != seat.getOperation()) {
                        canOperation[0] = false;
                        return;
                    }
                }
//                } else {
//                    if (MahjongUtil.checkChi(seat.getCards(), card[0], bao) && 4 != seat.getOperation()) {
//                        canOperation[0] = false;
//                        return;
//                    }
//                }
            }
        });

        return canOperation[0];
    }

    private boolean seatBetween(int seatNo, int operationSeatNo, int no) {
        if (operationSeatNo > no) {
            return seatNo < no || seatNo > operationSeatNo;
        } else {
            return seatNo < no && seatNo > operationSeatNo;
        }
    }

    /**
     * 当有人碰、杠后，再次检查是否还有人胡
     */
    public boolean checkCanPeng() {
        //找到那张牌
        final Integer[] card = new Integer[1];
        seats.stream().filter(seat -> seat.getSeatNo() == operationSeatNo)
                .forEach(seat -> card[0] = seat.getPlayedCards().get(seat.getPlayedCards().size() - 1));
        final boolean[] canOperation = {true};
        //先检查胡，胡优先
        seats.stream().filter(seat -> seat.getSeatNo() != operationSeatNo).forEach(seat -> {
            List<Integer> temp = new ArrayList<>();
            //当前玩家是否可以胡牌
            for (Integer card1 : seat.getCards()) {
                temp.add(card1 > 50 ? bao : card1);
            }
            temp.add(card[0] > 50 ? bao : card[0]);
            if (MahjongUtil.hu(temp, bao) && dianpao && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
//                if ((MahjongUtil.hu(temp, bao) || MahjongUtil.fei(temp, bao)) && dianpao && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
                canOperation[0] = false;
            }
        });

        return canOperation[0];
    }

    /**
     * 检查是否还需要操作
     */
    public boolean passedChecked() {
        //找到那张牌
        final Integer[] card = new Integer[1];
        seats.stream().filter(seat -> seat.getSeatNo() == operationSeatNo)
                .forEach(seat -> card[0] = seat.getPlayedCards().get(seat.getPlayedCards().size() - 1));
        final boolean[] hasNoOperation = {false};
        //先检查胡，胡优先
        seats.stream().filter(seat -> seat.getSeatNo() != operationSeatNo).forEach(seat -> {
            List<Integer> temp = new ArrayList<>();
            temp.addAll(seat.getCards());

            //当前玩家是否可以胡牌
            temp.add(card[0]);
//            if (dianpao && (MahjongUtil.fei(temp, bao) || MahjongUtil.hu(temp, bao)) && seat.getOperation() != 4) {
            if (dianpao && MahjongUtil.hu(temp, bao) && seat.getOperation() != 4 && !seat.isCanNotHu()) {
                hasNoOperation[0] = true;
                return;
            }

            //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
            int containSize = Card.containSize(temp, card[0]);
            if (4 == containSize && 0 < surplusCards.size() && seat.getOperation() != 4) {
                hasNoOperation[0] = true;
                return;
            } else if (3 <= containSize && seat.getOperation() != 4) {
                hasNoOperation[0] = true;
                return;
            }
//            if (30 > card[0] || (card[0] > 50 && bao < 30)) {
            //下家
            if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                if (MahjongUtil.checkChi(seat.getCards(), card[0], bao) && 4 != seat.getOperation()) {
                    hasNoOperation[0] = true;
                    return;
                }
            }
//            } else {
//                if (MahjongUtil.checkChi(seat.getCards(), card[0], bao) && 4 != seat.getOperation()) {
//                    hasNoOperation[0] = true;
//                    return;
//                }
//            }
        });

        return hasNoOperation[0];
    }

    /**
     * 碰或者港
     *
     * @param actionResponse
     * @param response
     * @param redisService
     */
    public void operation(GameBase.BaseAction.Builder actionResponse, GameBase.BaseConnection.Builder response, RedisService redisService) {
        //找到那张牌
        final Integer[] card = new Integer[1];
        Seat operationSeat = null;
        for (Seat seat : seats) {
            if (seat.getSeatNo() == operationSeatNo) {
                card[0] = seat.getPlayedCards().get(seat.getPlayedCards().size() - 1);
                operationSeat = seat;
                break;
            }
        }

        for (Seat seat : seats) {
            if (seat.getSeatNo() != operationSeatNo) {
                List<Integer> temp = new ArrayList<>();
                temp.addAll(seat.getCards());
                actionResponse.setID(seat.getUserId());
                //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
                int containSize = Card.containSize(temp, card[0]);
                if (3 == containSize && 0 < surplusCards.size() && seat.getOperation() == 2) {//杠牌
                    List<Integer> gangCards = new ArrayList<>();
                    gangCards.add(Card.remove(seat.getCards(), card[0]));
                    gangCards.add(Card.remove(seat.getCards(), card[0]));
                    gangCards.add(Card.remove(seat.getCards(), card[0]));
                    gangCards.add(card[0]);
                    //添加结算
                    List<ScoreType> scoreTypes = new ArrayList<>();
                    scoreTypes.add(ScoreType.DIAN_GANG);
                    operationSeat.getMingGangResult().add(new GameResult(scoreTypes, card[0], -((count - 1) * baseScore)));
                    seat.getMingGangCards().addAll(gangCards);
                    seat.getMingGangResult().add(new GameResult(scoreTypes, card[0], (count - 1) * baseScore));
                    seat.setMinggang(seat.getMinggang() + 1);
                    historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.DIAN_GANG, card[0]));

                    operationSeat.getPlayedCards().remove(operationSeat.getPlayedCards().size() - 1);
                    actionResponse.setID(seat.getUserId());
                    if (card[0] < 50) {
                        actionResponse.setOperationId(GameBase.ActionId.DIAN_GANG).setData(Mahjong.CardsData.newBuilder()
                                .addCards(card[0]).build().toByteString());
                    } else {
                        actionResponse.setOperationId(GameBase.ActionId.DIAN_GANG).setData(Mahjong.CardsData.newBuilder()
                                .addAllCards(gangCards).build().toByteString());
                    }
                    response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                    seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                            .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));

                    //点杠后需要摸牌
                    getCard(response, seat.getSeatNo(), redisService);
                    for (Seat seat1 : seats) {
                        seat1.setCanNotHu(false);
                    }
                    return;
                } else if (2 <= containSize && seat.getOperation() == 3) {//碰
                    List<Integer> pengCards = new ArrayList<>();
                    pengCards.add(Card.remove(seat.getCards(), card[0]));
                    pengCards.add(Card.remove(seat.getCards(), card[0]));
                    pengCards.add(card[0]);

                    operationSeatNo = seat.getSeatNo();
                    historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.PENG, card[0]));

                    operationSeat.getPlayedCards().remove(operationSeat.getPlayedCards().size() - 1);
                    seat.getPengCards().addAll(pengCards);
                    actionResponse.setID(seat.getUserId());
                    if (card[0] < 50) {
                        actionResponse.setOperationId(GameBase.ActionId.PENG).setData(Mahjong.CardsData.newBuilder().addCards(card[0]).build().toByteString());
                    } else {
                        actionResponse.setOperationId(GameBase.ActionId.PENG).setData(Mahjong.CardsData.newBuilder().addAllCards(pengCards).build().toByteString());
                    }
                    response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                    seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                            .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                    if (redisService.exists("room_match" + roomNo)) {
                        new PlayCardTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService).start();
                    }
                    GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder()
                            .setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0).setID(seat.getUserId()).build();
                    response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());
                    seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                            .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                    for (Seat seat1 : seats) {
                        seat1.setCanNotHu(false);
                    }
                    return;
                }
            }
        }

//        fei(response, redisService);

        int s = operationSeatNo;
        for (int i = 0; i < 3; i++) {
            s++;
            if (s > count) {
                s = 1;
            }
            for (Seat seat : seats) {
                if (seat.getSeatNo() == s) {
                    if (0 != seat.getChiTemp().size()) {
                        List<Integer> chiCard = new ArrayList<>();
                        Card.removeAll(seat.getCards(), seat.getChiTemp());
                        chiCard.addAll(seat.getChiTemp());
                        chiCard.add(card[0]);
                        seat.getChiCards().addAll(chiCard);

                        operationSeatNo = seat.getSeatNo();
                        historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.CHI, chiCard));

                        operationSeat.getPlayedCards().remove(operationSeat.getPlayedCards().size() - 1);
                        actionResponse.setID(seat.getUserId());
                        actionResponse.setOperationId(GameBase.ActionId.CHI).setData(Mahjong.CardsData.newBuilder().addAllCards(chiCard).build().toByteString());
                        response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                        seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                                .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                        if (redisService.exists("room_match" + roomNo)) {
                            new PlayCardTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService).start();
                        }
                        GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder()
                                .setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0).setID(seat.getUserId()).build();
                        response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());
                        seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                                .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                        for (Seat seat1 : seats) {
                            seat1.setCanNotHu(false);
                        }
                        return;
                    }
                    break;
                }
            }
        }

    }

    public void start(GameBase.BaseConnection.Builder response, RedisService redisService) {
        gameCount++;
        gameStatus = GameStatus.PLAYING;
        dealCard();
        //骰子
        int dice1 = new Random().nextInt(6) + 1;
        int dice2 = new Random().nextInt(6) + 1;
        dice = new Integer[]{dice1, dice2};
        Mahjong.MahjongStartResponse.Builder dealCard = Mahjong.MahjongStartResponse.newBuilder().setSurplusCardsSize(surplusCards.size());
        dealCard.setBanker(banker).addDice(dice1).addDice(dice2);
        dealCard.setRogue(jiabao);
        response.setOperationType(GameBase.OperationType.START);
        seats.stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId())).forEach(seat -> {
            dealCard.clearCards();
            dealCard.addAllCards(seat.getCards());
            response.setData(dealCard.build().toByteString());
            MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
        });

        GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder()
                .setTimeCounter(redisService.exists("room_match" + roomNo) ? 8 : 0).setID(banker).build();
        response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());

        if (redisService.exists("room_match" + roomNo)) {
            new PlayCardTimeout(banker, roomNo, historyList.size(), gameCount, redisService).start();
        }
        Seat operationSeat = null;
        for (Seat seat : seats) {
            if (operationSeatNo == seat.getSeatNo()) {
                operationSeat = seat;
            }
            if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
            }
        }

        checkSelfGetCard(response, operationSeat, redisService);
    }

    public void playCard(Integer card, int userId, GameBase.BaseAction.Builder actionResponse, GameBase.BaseConnection.Builder response, RedisService redisService) {
        actionResponse.setID(userId);
        for (Seat seat : seats) {
            if (seat.getUserId() == userId) {
                if (operationSeatNo == seat.getSeatNo() && lastOperation != userId) {
                    if (seat.getCards().contains(card)) {
                        seat.getCards().remove(card);
                        if (null == seat.getPlayedCards()) {
                            seat.setPlayedCards(new ArrayList<>());
                        }
                        seat.getPlayedCards().add(card);
                        Mahjong.CardsData.Builder builder = Mahjong.CardsData.newBuilder().addCards(card);

                        actionResponse.setOperationId(GameBase.ActionId.PLAY_CARD).setData(builder.build().toByteString());

                        response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                        lastOperation = userId;
                        historyList.add(new OperationHistory(userId, OperationHistoryType.PLAY_CARD, card));
                        if (historyList.size() == 7 && 4 == count) {
                            int playCard = historyList.get(0).getCards().get(0);
                            qianShao = true;
                            for (int i = 1; i < 7; i++) {
                                if (i % 2 == 1 && 0 != historyList.get(i).getHistoryType().compareTo(OperationHistoryType.GET_CARD)) {
                                    qianShao = false;
                                    break;
                                } else {
                                    if (playCard != historyList.get(i).getCards().get(0)) {
                                        qianShao = false;
                                        break;
                                    }
                                }
                            }
                        }
                        seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                                .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                        //先检查其它三家牌，是否有人能胡、杠、碰
                        checkCard(card, response, redisService);
                    } else {
                        System.out.println("用户手中没有此牌" + userId);
                    }
                } else {
                    System.out.println("不该当前玩家操作" + userId);
                }
                break;
            }
        }
    }

    public void sendRoomInfo(GameBase.RoomCardIntoResponse.Builder roomCardIntoResponseBuilder, GameBase.BaseConnection.Builder response, int userId) {
        Ruijin.RuijinMahjongIntoResponse.Builder intoResponseBuilder = Ruijin.RuijinMahjongIntoResponse.newBuilder();
        intoResponseBuilder.setBaseScore(baseScore);
        intoResponseBuilder.setCount(count);
        intoResponseBuilder.setGameTimes(gameTimes);
        intoResponseBuilder.setDianpao(dianpao);
        intoResponseBuilder.setZhuangxian(zhuangxian);
        roomCardIntoResponseBuilder.setGameType(GameBase.GameType.MAHJONG_RUIJIN);
        roomCardIntoResponseBuilder.setError(GameBase.ErrorCode.SUCCESS);
        roomCardIntoResponseBuilder.setData(intoResponseBuilder.build().toByteString());
        response.setOperationType(GameBase.OperationType.ROOM_INFO).setData(roomCardIntoResponseBuilder.build().toByteString());
        if (MahjongTcpService.userClients.containsKey(userId)) {
            MahjongTcpService.userClients.get(userId).send(response.build(), userId);
        }
    }

    public void sendSeatInfo(GameBase.BaseConnection.Builder response) {
        GameBase.RoomSeatsInfo.Builder roomSeatsInfo = GameBase.RoomSeatsInfo.newBuilder();
        for (Seat seat1 : seats) {
            GameBase.SeatResponse.Builder seatResponse = GameBase.SeatResponse.newBuilder();
            seatResponse.setSeatNo(seat1.getSeatNo());
            seatResponse.setID(seat1.getUserId());
            seatResponse.setScore(seat1.getScore());
            seatResponse.setReady(seat1.isReady());
            seatResponse.setNickname(seat1.getNickname());
            seatResponse.setHead(seat1.getHead());
            seatResponse.setSex(seat1.isSex());
            seatResponse.setOffline(seat1.isRobot());
            seatResponse.setIsRobot(seat1.isRobot());
            seatResponse.setIp(seat1.getIp());
            seatResponse.setGameCount(seat1.getGameCount());
            roomSeatsInfo.addSeats(seatResponse.build());
        }
        response.setOperationType(GameBase.OperationType.SEAT_INFO).setData(roomSeatsInfo.build().toByteString());
        for (Seat seat : seats) {
            if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
            }
        }
    }

    public boolean checkHu(GameBase.BaseConnection.Builder response, RedisService redisService) {
        Seat loseSeat = null;
        for (Seat seat : seats) {
            if (seat.getSeatNo() == operationSeatNo) {
                loseSeat = seat;
                break;
            }
        }
        List<Seat> huShun = new ArrayList<>();
        for (Seat seat1 : seats) {
            if (MahjongUtil.hu(seat1.getCards(), bao) && seat1.getSeatNo() > loseSeat.getSeatNo()) {
                huShun.add(seat1);
            }
        }
        for (Seat seat1 : seats) {
            if (MahjongUtil.hu(seat1.getCards(), bao) && seat1.getSeatNo() < loseSeat.getSeatNo()) {
                huShun.add(seat1);
            }
        }
        for (Seat seat1 : huShun) {
            if (seat1.getOperation() == 0) {
                return false;
            } else if (1 == seat1.getOperation()) {
                hu(seat1.getUserId(), response, redisService);
                return true;
            }
        }
        return false;
    }
}
