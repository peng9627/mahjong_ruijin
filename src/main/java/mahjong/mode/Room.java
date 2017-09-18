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

    public void addSeat(User user, int score) {
        Seat seat = new Seat();
        seat.setRobot(false);
        seat.setReady(false);
        seat.setAreaString(user.getArea());
        seat.setHead(user.getHead());
        seat.setNickname(user.getNickname());
        seat.setSex(user.getSex().equals("MAN"));
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
            if (banker == seat.getUserId()) {
//                int cardIndex = 0;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex += 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
//                cardIndex = 3;
//                cardList.add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);
                for (int i = 0; i < 14; i++) {
                    int cardIndex = 0;
                    cardList.add(surplusCards.get(cardIndex));
                    surplusCards.remove(cardIndex);
                }
            } else {
                for (int i = 0; i < 13; i++) {
                    int cardIndex = (int) (Math.random() * surplusCards.size());
                    cardList.add(surplusCards.get(cardIndex));
                    surplusCards.remove(cardIndex);
                }
            }
            seat.setCards(cardList);
            seat.setInitialCards(cardList);

            if (seat.getUserId() == banker) {
                operationSeatNo = seat.getSeatNo();
//                int cardIndex = 8;
                int cardIndex = (int) (Math.random() * surplusCards.size());
//                seat.getCards().add(surplusCards.get(cardIndex));
//                surplusCards.remove(cardIndex);

//                cardIndex = (int) (Math.random() * surplusCards.size());
                jiabao = surplusCards.get(cardIndex);
                surplusCards.remove(cardIndex);
            }

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

    private void clear() {
        Record record = new Record();
        record.setJiabao(jiabao);
        record.setDice(dice);
        record.setBanker(banker);
        List<SeatRecord> seatRecords = new ArrayList<>();
        seats.forEach(seat -> {
            SeatRecord seatRecord = new SeatRecord();
            seatRecord.setUserId(seat.getUserId());
            seatRecord.setNickname(seat.getNickname());
            seatRecord.setHead(seat.getHead());
            seatRecord.setCardResult(seat.getCardResult());
            seatRecord.getGangResult().addAll(seat.getGangResult());
            seatRecord.getInitialCards().addAll(seat.getInitialCards());
            seatRecord.getCards().addAll(seat.getCards());
            final int[] winOrLose = {0};
            seat.getGangResult().forEach(gameResult -> winOrLose[0] += gameResult.getScore());
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
    }

    public void getCard(GameBase.BaseConnection.Builder response, int seatNo, RedisService redisService) {
        if (0 == surplusCards.size()) {
            gameOver(response, redisService);
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
        Mahjong.MahjongGetCardResponse.Builder builder1 = Mahjong.MahjongGetCardResponse.newBuilder();
        builder1.setCard(card1);

        Seat operationSeat = null;
        for (Seat seat : seats) {
            if (seat.getSeatNo() == seatNo) {
                seat.getCards().add(card1);
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
    public void gameOver(GameBase.BaseConnection.Builder response, RedisService redisService) {

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
                userResult.setCardScore(seat.getCardResult().getScore());
                win[0] += seat.getCardResult().getScore();
                if (seat.getCardResult().getScore() > 0) {
                    userResult.setCardScore(seat.getCardResult().getScore());
                    if (1 == zhuangxian) {
                        banker = seat.getUserId();
                    }
                } else {
                    loseSeats.add(seat.getSeatNo());
                    score[0] = seat.getCardResult().getScore();
                }
                for (ScoreType scoreType : seat.getCardResult().getScoreTypes()) {
                    userResult.addScoreTypes(Mahjong.ScoreType.forNumber(scoreType.ordinal() + 3));
                }
            }
            List<Integer> gangCard = new ArrayList<>();
            int gangScore = 0;
            for (GameResult gameResult : seat.getGangResult()) {
                gangScore += gameResult.getScore();
                if (0 < gameResult.getScore()) {
                    gangCard.add(gameResult.getCard());
                }
            }
            userResult.setGangScore(gangScore);
            win[0] += gangScore;

            userResult.setWinOrLose(win[0]);
            resultResponse.addUserResult(userResult);

            seat.setScore(seat.getScore() + win[0]);

        });

        response.setOperationType(GameBase.OperationType.RESULT).setData(resultResponse.build().toByteString());
        seats.stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId()))
                .forEach(seat -> MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId()));

        clear();
        //结束房间
        if (gameCount == gameTimes) {
            roomOver(response, redisService);
        } else {
            if (redisService.exists("room_match" + roomNo)) {
                new ReadyTimeout(Integer.valueOf(roomNo), redisService).start();
            }
        }
    }

    public void roomOver(GameBase.BaseConnection.Builder response, RedisService redisService) {
        if (0 == gameStatus.compareTo(GameStatus.WAITING)) {
            JSONObject jsonObject = new JSONObject();
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
            ApiResponse moneyDetail = JSON.parseObject(HttpUtil.urlConnectionByRsa("http://127.0.0.1:9999/api/money_detailed/create", jsonObject.toJSONString()), new TypeReference<ApiResponse<User>>() {
            });
            if (0 != moneyDetail.getCode()) {
                LoggerFactory.getLogger(this.getClass()).error("http://127.0.0.1:9999/api/money_detailed/create?" + jsonObject.toJSONString());
            }
        }
        Mahjong.MahjongOverResponse.Builder over = Mahjong.MahjongOverResponse.newBuilder();

        for (Seat seat : seats) {
            Mahjong.MahjongSeatGameOver.Builder seatGameOver = Mahjong.MahjongSeatGameOver.newBuilder()
                    .setID(seat.getUserId()).setMinggang(seat.getMinggang()).setAngang(seat.getAngang())
                    .setZimoCount(seat.getZimoCount()).setHuCount(seat.getHuCount())
                    .setDianpaoCount(seat.getDianpaoCount()).setWinOrLose(seat.getScore());
            over.addGameOver(seatGameOver);
        }

        StringBuilder people = new StringBuilder();

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

        JSONObject jsonObject = new JSONObject();
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

            jsonObject.put("gameType", 1);
            jsonObject.put("roomOwner", roomOwner);
            jsonObject.put("people", people.toString().substring(1));
            jsonObject.put("gameTotal", gameTimes);
            jsonObject.put("gameCount", gameCount);
            jsonObject.put("peopleCount", count);
            jsonObject.put("roomNo", Integer.parseInt(roomNo));
            jsonObject.put("gameData", JSON.toJSONString(recordList, feature, features).getBytes());
            jsonObject.put("scoreData", JSON.toJSONString(totalScores, feature, features).getBytes());

            ApiResponse apiResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa(Constant.apiUrl + Constant.gamerecordCreateUrl, jsonObject.toJSONString()), ApiResponse.class);
            if (0 != apiResponse.getCode()) {
                LoggerFactory.getLogger(this.getClass()).error(Constant.apiUrl + Constant.gamerecordCreateUrl + "?" + jsonObject.toJSONString());
            }
        }
        //是否竞技场
        if (redisService.exists("room_match" + roomNo)) {
            String matchNo = redisService.getCache("room_match" + roomNo);
            redisService.delete("room_match" + roomNo);
            if (redisService.exists("match_info" + matchNo)) {
                while (!redisService.lock("lock_match_info" + matchNo)) {
                }
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
                    for (MatchUser matchUser : matchUsers) {
                        if (seat.getUserId() == matchUser.getUserId()) {
                            matchUser.setScore(seat.getScore());
                        }
                    }
                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.setOperationType(GameBase.OperationType.ROOM_INFO).clearData().build(), seat.getUserId());
                        GameBase.RoomSeatsInfo.Builder roomSeatsInfo = GameBase.RoomSeatsInfo.newBuilder();
                        GameBase.SeatResponse.Builder seatResponse = GameBase.SeatResponse.newBuilder();
                        seatResponse.setSeatNo(1);
                        seatResponse.setID(seat.getUserId());
                        seatResponse.setScore(seat.getScore());
                        seatResponse.setReady(false);
                        seatResponse.setIp(seat.getIp());
                        seatResponse.setGameCount(seat.getGameCount());
                        seatResponse.setNickname(seat.getNickname());
                        seatResponse.setHead(seat.getHead());
                        seatResponse.setSex(seat.isSex());
                        seatResponse.setOffline(false);
                        roomSeatsInfo.addSeats(seatResponse.build());
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.setOperationType(GameBase.OperationType.SEAT_INFO).setData(roomSeatsInfo.build().toByteString()).build(), seat.getUserId());
                    }
                }

                //用户对应分数
                Map<Integer, Integer> userIdScore = new HashMap<>();
                for (MatchUser matchUser : matchUsers) {
                    userIdScore.put(matchUser.getUserId(), matchUser.getScore());
                }

                GameBase.MatchData.Builder matchData = GameBase.MatchData.newBuilder().setStartDate(matchInfo.getStartDate().getTime());
                switch (matchInfo.getStatus()) {
                    case 1:
                        //TODO 少一个0，记得加回来
                        int addScoreCount = (int) ((new Date().getTime() - matchInfo.getStartDate().getTime()) / 12000);

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
                                    if (seat.getScore() < 500 + (addScoreCount * 100) && matchUsers.size() > arena.getCount() / 2) {
                                        matchUsers.remove(matchUser);
                                        response.setOperationType(GameBase.OperationType.MATCH_RESULT).setData(GameBase.MatchResult.newBuilder()
                                                .setRanking(matchUsers.size()).build().toByteString());
                                        if (MahjongTcpService.userClients.containsKey(matchUser.getUserId())) {
                                            MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                                        }
                                        redisService.delete("reconnect" + seat.getUserId());
                                    } else {
                                        thisWait.add(matchUser);
                                        redisService.addCache("reconnect" + seat.getUserId(), "sangong," + matchNo);
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
                                rooms.add(matchInfo.addRoom(matchNo, 1, redisService, users, userIdScore, response, matchData));
                            }
                        }
                        break;
                    case 2:
                    case 3:
                        for (Seat seat : seats) {
                            redisService.addCache("reconnect" + seat.getUserId(), "sangong," + matchNo);
                        }
                        if (0 == rooms.size()) {
                            matchInfo.setStatus(matchInfo.getStatus() + 1);
                            matchData.setStatus(matchInfo.getStatus());

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
                            redisService.addCache("reconnect" + seat.getUserId(), "sangong," + matchNo);
                        }

                        waitUsers.sort(new Comparator<MatchUser>() {
                            @Override
                            public int compare(MatchUser o1, MatchUser o2) {
                                return o1.getScore() > o2.getScore() ? -1 : 1;
                            }
                        });
                        while (waitUsers.size() > 4) {
                            MatchUser matchUser = waitUsers.remove(waitUsers.size() - 1);

                            response.setOperationType(GameBase.OperationType.MATCH_RESULT).setData(GameBase.MatchResult.newBuilder()
                                    .setRanking(matchUsers.size()).build().toByteString());
                            if (MahjongTcpService.userClients.containsKey(matchUser.getUserId())) {
                                MahjongTcpService.userClients.get(matchUser.getUserId()).send(response.build(), matchUser.getUserId());
                            }
                            redisService.delete("reconnect" + matchUser.getUserId());
                        }

                        if (0 == rooms.size()) {

                            matchUsers.clear();
                            matchUsers.addAll(waitUsers);

                            matchInfo.setStatus(5);
                            matchData.setStatus(5);

                            List<User> users = new ArrayList<>();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < matchUsers.size(); i++) {
                                stringBuilder.append(",").append(matchUsers.get(i).getUserId());
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
                            response.setOperationType(GameBase.OperationType.MATCH_RESULT).setData(GameBase.MatchResult.newBuilder().setRanking(i + 1).build().toByteString());
                            if (MahjongTcpService.userClients.containsKey(matchUsers.get(i).getUserId())) {
                                MahjongTcpService.userClients.get(matchUsers.get(i).getUserId()).send(response.build(), matchUsers.get(i).getUserId());
                            }
                        }
                        matchInfo.setStatus(-1);
                        break;
                }
                if (0 < matchInfo.getStatus()) {
                    matchInfo.setRooms(rooms);
                    matchInfo.setWaitUsers(waitUsers);
                    redisService.addCache("match_info" + matchNo, JSON.toJSONString(matchInfo));
                }
                redisService.unlock("lock_match_info" + matchNo);
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
        if (MahjongUtil.fei(seat.getCards(), bao, jiabao) || MahjongUtil.hu(seat.getCards(), false)) {
            builder.addOperationId(GameBase.ActionId.HU);
            if (redisService.exists("room_match" + roomNo)) {
                new OperationTimeout(seat.getUserId(), roomNo, historyList.size(), gameCount, redisService, true).start();
            }
        }
        //暗杠
        if (null != MahjongUtil.checkGang(seat.getCards())) {
            builder.addOperationId(GameBase.ActionId.AN_GANG);
        }
        //扒杠
        if (null != MahjongUtil.checkBaGang(seat.getCards(), seat.getPengCards())) {
            builder.addOperationId(GameBase.ActionId.BA_GANG);
        }
        if (0 != builder.getOperationIdCount()) {
            if (redisService.exists("room_match" + roomNo)) {
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
            temp.add(card1 > 50 ? jiabao : card1);
        }
        //检查是自摸还是点炮,自摸输家是其它三家
        if (MahjongUtil.fei(huSeat[0].getCards(), bao, jiabao) || MahjongUtil.hu(huSeat[0].getCards(), false)) {

            ScoreType scoreType = MahjongUtil.getHuType(huSeat[0].getCards(), bao);
            int score = MahjongUtil.getScore(scoreType);

            //天胡
            if (historyList.size() == 0 && score < 20) {
                scoreType = ScoreType.TIANHU;
                score = 10;
            } else {
                scoreType = ScoreType.ZIMO;
                score += 1;
            }
            int loseSize[] = {0};
            score *= baseScore;
            scoreTypes.add(scoreType);
            int finalScore = score;
            seats.stream().filter(seat -> seat.getUserId() != userId)
                    .forEach(seat -> {
                        seat.setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), -finalScore));
                        loseSize[0]++;
                    });

            huSeat[0].setCardResult(new GameResult(scoreTypes, huSeat[0].getCards().get(huSeat[0].getCards().size() - 1), loseSize[0] * score));
            huSeat[0].setZimoCount(huSeat[0].getZimoCount() + 1);
            gameOver(response, redisService);

            return;
        }

        if (dianpao && 0 == Card.containSize(huSeat[0].getCards(), bao)) {
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
                        temp.add(card1 > 50 ? jiabao : card1);
                    }
                    //当前玩家是否可以胡牌
                    temp.add(card > 50 ? jiabao : card);
                    if (MahjongUtil.hu(temp, false)) {
                        ScoreType scoreType = ScoreType.PINGHU;
                        int score = MahjongUtil.getScore(scoreType);
                        //地胡
                        if (historyList.size() == 1 && score < 20) {
                            scoreType = ScoreType.DIHU;
                            score = 10;
                        }
                        score *= baseScore;
                        scoreTypes.add(scoreType);

                        loseSeat.setCardResult(new GameResult(scoreTypes, card, -score));
                        loseSeat.setDianpaoCount(loseSeat.getDianpaoCount() + 1);
                        seat.setCardResult(new GameResult(scoreTypes, card, score));
                        seat.setHuCount(seat.getHuCount() + 1);
                        hu = true;
                    }
                }
            }
            if (hu) {
                //胡牌
                gameOver(response, redisService);
                return;
            }
        }

        if (checkCanChi(huSeat[0].getSeatNo())) {
            fei(response, redisService);
        }

    }

    /**
     * 飞
     */
    public void fei(GameBase.BaseConnection.Builder response, RedisService redisService) {
        //和牌的人
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
                    List<Integer> temp = new ArrayList<>();
                    temp.addAll(seat.getCards());
                    //当前玩家是否可以胡牌
                    temp.add(card > 50 ? jiabao : card);
                    List<ScoreType> scoreTypes = new ArrayList<>();
                    if (MahjongUtil.fei(temp, bao, jiabao)) {
                        ScoreType scoreType = ScoreType.FEI;
                        int score = MahjongUtil.getScore(scoreType);
                        //地胡
                        if (historyList.size() == 1 && score < 20) {
                            scoreType = ScoreType.DIHU;
                            score = 10;
                        }
                        score *= baseScore;
                        scoreTypes.add(scoreType);

                        loseSeat.setCardResult(new GameResult(scoreTypes, card, -score));
                        loseSeat.setDianpaoCount(loseSeat.getDianpaoCount() + 1);
                        seat.setCardResult(new GameResult(scoreTypes, card, score));
                        seat.setHuCount(seat.getHuCount() + 1);
                        hu = true;
                    }
                }
            }
            if (hu) {
                //胡牌
                gameOver(response, redisService);
            }
        }

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
                Card.remove(seat.getCards(), card);
                Card.remove(seat.getCards(), card);
                Card.remove(seat.getCards(), card);
                Card.remove(seat.getCards(), card);

                seat.getAnGangCards().add(card);

                List<ScoreType> scoreTypes = new ArrayList<>();
                scoreTypes.add(ScoreType.AN_GANG);

                final int[] loseSize = {0};
                seats.stream().filter(seat1 -> seat1.getSeatNo() != seat.getSeatNo())
                        .forEach(seat1 -> {
                            seat1.getGangResult().add(new GameResult(scoreTypes, card, -(2 * baseScore)));
                            loseSize[0]++;
                        });
                seat.getGangResult().add(new GameResult(scoreTypes, card, (2 * baseScore) * loseSize[0]));
                seat.setAngang(seat.getAngang() + 1);
                historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.AN_GANG, card));

                actionResponse.setOperationId(GameBase.ActionId.AN_GANG).setData(Mahjong.MahjongGang.newBuilder().setCard(card).build().toByteString());
                response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                        .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                getCard(response, seat.getSeatNo(), redisService);
            } else if (1 == Card.containSize(seat.getPengCards(), card) && 1 == Card.containSize(seat.getCards(), card)) {//扒杠
                Card.remove(seat.getCards(), card);
                Card.remove(seat.getPengCards(), card);


                seat.getMingGangCards().add(card);

                List<ScoreType> scoreTypes = new ArrayList<>();
                scoreTypes.add(ScoreType.BA_GANG);

                final int[] loseSize = {0};
                seats.stream().filter(seat1 -> seat1.getSeatNo() != seat.getSeatNo())
                        .forEach(seat1 -> {
                            seat1.getGangResult().add(new GameResult(scoreTypes, card, -baseScore));
                            loseSize[0]++;
                        });
                seat.getGangResult().add(new GameResult(scoreTypes, card, loseSize[0] * baseScore));
                seat.setMinggang(seat.getMinggang() + 1);
                historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.BA_GANG, card));

                actionResponse.setOperationId(GameBase.ActionId.BA_GANG).setData(Mahjong.MahjongGang.newBuilder().setCard(card).build().toByteString());
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
                if (30 > card) {
                    //下家
                    if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                        if (MahjongUtil.checkChi(seat.getCards(), card, jiabao)) {
                            builder.addOperationId(GameBase.ActionId.CHI);
                        }
                    }
                } else {
                    if (MahjongUtil.checkChi(seat.getCards(), card, jiabao)) {
                        builder.addOperationId(GameBase.ActionId.CHI);
                    }
                }
                //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
                int containSize = Card.containSize(temp, card);
                if (3 == containSize && 0 < surplusCards.size()) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                    builder.addOperationId(GameBase.ActionId.DIAN_GANG);
                } else if (2 == containSize) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                }
                //当前玩家是否可以胡牌
                temp.add(card);
                if (dianpao && MahjongUtil.fei(temp, bao, jiabao) || MahjongUtil.hu(temp, false)) {
                    builder.addOperationId(GameBase.ActionId.HU);
                }
                if (0 != builder.getOperationIdCount()) {
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
                if (30 > card) {
                    //下家
                    if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                        if (MahjongUtil.checkChi(seat.getCards(), card, jiabao)) {
                            builder.addOperationId(GameBase.ActionId.CHI);
                        }
                    }
                } else {
                    if (MahjongUtil.checkChi(seat.getCards(), card, jiabao)) {
                        builder.addOperationId(GameBase.ActionId.CHI);
                    }
                }
                //当前玩家手里有几张牌，3张可碰可杠，两张只能碰
                int containSize = Card.containSize(temp, card);
                if (3 == containSize && 0 < surplusCards.size()) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                    builder.addOperationId(GameBase.ActionId.DIAN_GANG);
                } else if (2 == containSize) {
                    builder.addOperationId(GameBase.ActionId.PENG);
                }
                //当前玩家是否可以胡牌
                temp.add(card);
                if (dianpao && MahjongUtil.fei(temp, bao, jiabao) || MahjongUtil.hu(temp, false)) {
                    builder.addOperationId(GameBase.ActionId.HU);
                }
                if (0 != builder.getOperationIdCount()) {
                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                        response.setOperationType(GameBase.OperationType.ASK).setData(builder.build().toByteString());
                        MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                    }
                    //TODO 出牌超时
//                new OperationTimeout(deskNo, card).start();
                }
            });
        }
    }

    /**
     * 当有人吃后，再次检查是否还有人胡、碰、杠
     */
    public boolean checkCanChi(int seatNo) {
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
                temp.add(card1 > 50 ? jiabao : card1);
            }
            temp.add(card[0] > 50 ? jiabao : card[0]);
            if ((MahjongUtil.hu(temp, false) && dianpao) && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
                canOperation[0] = false;
                return;
            }
            if (10 == seatNo) {
                return;
            }
            if (seatBetween(seat.getSeatNo(), operationSeatNo, seatNo)) {
                if (30 > card[0]) {
                    //下家
                    if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                        if (MahjongUtil.checkChi(seat.getCards(), card[0], jiabao) && 4 != seat.getOperation()) {
                            canOperation[0] = false;
                            return;
                        }
                    }
                } else {
                    if (30 > card[0]) {
                        //下家
                        if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                            if (MahjongUtil.checkChi(seat.getCards(), card[0], jiabao) && 4 != seat.getOperation()) {
                                canOperation[0] = false;
                                return;
                            }
                        }
                    } else {
                        if (MahjongUtil.checkChi(seat.getCards(), card[0], jiabao) && 4 != seat.getOperation()) {
                            canOperation[0] = false;
                            return;
                        }
                    }
                }
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
                temp.add(card1 > 50 ? jiabao : card1);
            }
            temp.add(card[0] > 50 ? jiabao : card[0]);
            if (MahjongUtil.hu(temp, false) && dianpao && seat.getOperation() == 0 && 0 == seat.getChiTemp().size()) {
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
            if (dianpao && (MahjongUtil.fei(temp, bao, jiabao) || MahjongUtil.hu(temp, false)) && seat.getOperation() != 4) {
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
            if (30 > card[0]) {
                //下家
                if (seat.getSeatNo() == getSeat(operationSeatNo, 1)) {
                    if (MahjongUtil.checkChi(seat.getCards(), card[0], jiabao) && 4 != seat.getOperation()) {
                        hasNoOperation[0] = true;
                        return;
                    }
                }
            } else {
                if (MahjongUtil.checkChi(seat.getCards(), card[0], jiabao) && 4 != seat.getOperation()) {
                    hasNoOperation[0] = true;
                    return;
                }
            }
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
                    Card.remove(seat.getCards(), card[0]);
                    Card.remove(seat.getCards(), card[0]);
                    Card.remove(seat.getCards(), card[0]);
                    seat.getMingGangCards().add(card[0]);

                    //添加结算
                    List<ScoreType> scoreTypes = new ArrayList<>();
                    scoreTypes.add(ScoreType.DIAN_GANG);
                    operationSeat.getGangResult().add(new GameResult(scoreTypes, card[0], -(3 * baseScore)));
                    seat.getGangResult().add(new GameResult(scoreTypes, card[0], 3 * baseScore));
                    seat.setMinggang(seat.getMinggang() + 1);
                    historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.DIAN_GANG, card[0]));

                    operationSeat.getPlayedCards().remove(operationSeat.getPlayedCards().size() - 1);
                    actionResponse.setID(seat.getUserId());
                    actionResponse.setOperationId(GameBase.ActionId.DIAN_GANG).setData(Mahjong.MahjongGang.newBuilder()
                            .setCard(card[0]).build().toByteString());
                    response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                    seats.stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId()))
                            .forEach(seat1 -> MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));

                    //点杠后需要摸牌
                    getCard(response, seat.getSeatNo(), redisService);
                    return;
                } else if (2 <= containSize && seat.getOperation() == 3) {//碰
                    Card.remove(seat.getCards(), card[0]);
                    Card.remove(seat.getCards(), card[0]);
                    seat.getPengCards().add(card[0]);
                    operationSeatNo = seat.getSeatNo();
                    historyList.add(new OperationHistory(seat.getUserId(), OperationHistoryType.PENG, card[0]));

                    operationSeat.getPlayedCards().remove(operationSeat.getPlayedCards().size() - 1);
                    actionResponse.setID(seat.getUserId());
                    actionResponse.setOperationId(GameBase.ActionId.PENG).setData(Mahjong.MahjongPengResponse.newBuilder().setCard(card[0]).build().toByteString());
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
                    return;
                }
            }
        }

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
                        actionResponse.setOperationId(GameBase.ActionId.CHI).setData(Mahjong.MahjongChi.newBuilder().addAllCards(chiCard).build().toByteString());
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
                        return;
                    }
                    break;
                }
            }
        }

        fei(response, redisService);
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
        Ruijin.RuijinStartResponse.Builder ruijinDealCard = Ruijin.RuijinStartResponse.newBuilder();
        ruijinDealCard.setRogue(jiabao);
        dealCard.setBanker(banker).addDice(dice1).addDice(dice2);
        ruijinDealCard.setRogue(jiabao);
        response.setOperationType(GameBase.OperationType.START);
        seats.stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId())).forEach(seat -> {
            dealCard.clearCards();
            dealCard.addAllCards(seat.getCards());
            ruijinDealCard.setMahjongStartResponse(dealCard);
            response.setData(ruijinDealCard.build().toByteString());
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
                        Mahjong.MahjongPlayCard.Builder builder = Mahjong.MahjongPlayCard.newBuilder().setCard(card);

                        actionResponse.setOperationId(GameBase.ActionId.PLAY_CARD).setData(builder.build().toByteString());

                        response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                        lastOperation = userId;
                        historyList.add(new OperationHistory(userId, OperationHistoryType.PLAY_CARD, card));
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
}
