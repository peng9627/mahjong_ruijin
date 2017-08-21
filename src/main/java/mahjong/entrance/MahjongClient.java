package mahjong.entrance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.protobuf.GeneratedMessageV3;
import mahjong.mode.*;
import mahjong.redis.RedisService;
import mahjong.utils.ByteUtils;
import mahjong.utils.CoreStringUtils;
import mahjong.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created date 2016/3/25
 * Author pengyi
 */
public class MahjongClient implements Runnable {

    private final InputStream is;
    private final OutputStream os;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Socket s;
    private int userId;
    private RedisService redisService;
    private String roomNo;
    private Boolean connect;
    private byte[] md5Key = "2704031cd4814eb2a82e47bd1d9042c6".getBytes();

    private GameBase.BaseConnection request;
    private GameBase.BaseConnection.Builder response;

    private List<User> users = new ArrayList<>();

    MahjongClient(Socket s, RedisService redisService) {

        this.s = s;
        connect = true;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = s.getInputStream();
            outputStream = s.getOutputStream();
            this.redisService = redisService;
        } catch (EOFException e) {
            logger.info("socket.shutdown.message");
            close();
        } catch (IOException e) {
            logger.info("socket.connection.fail.message" + e.getMessage());
            close();
        }
        is = inputStream;
        os = outputStream;
        request = GameBase.BaseConnection.newBuilder().build();
        response = GameBase.BaseConnection.newBuilder();
    }

    public void send(GeneratedMessageV3 messageV3, int userId) {
        try {
            if (MahjongTcpService.userClients.containsKey(userId)) {
                synchronized (MahjongTcpService.userClients.get(userId).os) {
                    OutputStream os = MahjongTcpService.userClients.get(userId).os;
                    String md5 = CoreStringUtils.md5(ByteUtils.addAll(md5Key, messageV3.toByteArray()), 32, false);
                    messageV3.sendTo(os, md5);
                    logger.info("mahjong send:len=\n" + messageV3 + "\nuser=" + userId + "\n");
                }
            }
        } catch (IOException e) {
            logger.info("socket.server.sendMessage.fail.message" + userId + e.getMessage());
//            client.close();
        }
    }

    private void close() {
        connect = false;
        try {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
            if (s != null) {
                s.close();
            }
            if (0 != userId) {
//                exit();
                if (redisService.exists("room" + roomNo)) {
                    while (!redisService.lock("lock_room" + roomNo)) {
                        Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);

                        for (Seat seat : room.getSeats()) {
                            if (seat.getUserId() == userId) {
                                seat.setRobot(true);
                                break;
                            }
                        }

                        Mahjong.MahjongGameInfo.Builder gameInfo = Mahjong.MahjongGameInfo.newBuilder().setGameStatus(GameBase.GameStatus.PLAYING);
                        Seat operationSeat = null;
                        for (Seat seat : room.getSeats()) {
                            if (seat.getSeatNo() == room.getOperationSeatNo()) {
                                operationSeat = seat;
                                break;
                            }
                        }
                        gameInfo.setOperationUser(operationSeat.getUserId());
                        gameInfo.setLastOperationUser(room.getLastOperation());
                        for (Seat seat1 : room.getSeats()) {
                            Mahjong.MahjongSeatGameInfo.Builder seatResponse = Mahjong.MahjongSeatGameInfo.newBuilder();
                            seatResponse.setID(seat1.getUserId());
                            seatResponse.setIsRobot(seat1.isRobot());
                            if (null != seat1.getInitialCards()) {
                                if (seat1.getUserId() == userId) {
                                    seatResponse.addAllInitialCards(seat1.getInitialCards());
                                }
                            }
                            if (null != seat1.getCards()) {
                                if (seat1.getUserId() == userId) {
                                    seatResponse.addAllCards(seat1.getCards());
                                } else {
                                    seatResponse.setCardsSize(seat1.getCards().size());
                                }
                            }

                            if (null != seat1.getPengCards()) {
                                seatResponse.addAllPengCards(seat1.getPengCards());
                            }
                            if (null != seat1.getGangCards()) {
                                seatResponse.addAllGangCards(seat1.getGangCards());
                            }
                            if (null != seat1.getChiCards()) {
                                seatResponse.addAllChiCards(seat1.getChiCards());
                            }

                            if (null != seat1.getPlayedCards()) {
                                seatResponse.addAllPlayedCards(seat1.getPlayedCards());
                            }
                            gameInfo.addSeats(seatResponse.build());
                        }
                        Ruijin.RuijinMahjongGameInfo.Builder ruijinGameInfo = Ruijin.RuijinMahjongGameInfo.newBuilder().setMahjongGameInfo(gameInfo);
                        ruijinGameInfo.setRogue(room.getJiabao());
                        response.setOperationType(GameBase.OperationType.GAME_INFO).setData(ruijinGameInfo.build().toByteString());
                        send(response.build(), userId);

                        redisService.addCache("room" + roomNo, JSON.toJSONString(room));
                        redisService.unlock("lock_room" + roomNo);
                    }

                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private int readInt(InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        int ch3 = is.read();
        int ch4 = is.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24 | ((ch2 << 16) & 0xff) | ((ch3 << 8) & 0xff) | (ch4 & 0xFF));
    }

    private String readString(InputStream is) throws IOException {
        int len = readInt(is);
        byte[] bytes = new byte[len];
        is.read(bytes);
        return new String(bytes);
    }

    @Override
    public void run() {
        try {
            while (connect) {

                int len = readInt(is);
                String md5 = readString(is);
                len -= md5.getBytes().length + 4;
                byte[] data = new byte[len];
                boolean check = true;
                if (0 != len) {
                    check = len == is.read(data) && CoreStringUtils.md5(ByteUtils.addAll(md5Key, data), 32, false).equalsIgnoreCase(md5);
                }
                if (check) {
                    request = GameBase.BaseConnection.parseFrom(data);
                    logger.info(userId + "接收\n" + request + "\n");
                    switch (request.getOperationType()) {
                        case CONNECTION:
                            //加入玩家数据
                            if (redisService.exists("maintenance")) {
                                break;
                            }
                            GameBase.RoomCardIntoRequest intoRequest = GameBase.RoomCardIntoRequest.parseFrom(request.getData());

                            GameBase.RoomCardIntoResponse.Builder roomCardIntoResponseBuilder = GameBase.RoomCardIntoResponse.newBuilder();
                            roomCardIntoResponseBuilder.setGameType(GameBase.GameType.MAHJONG_RUIJIN);
                            userId = intoRequest.getID();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userId", userId);
                            ApiResponse<User> userResponse = JSON.parseObject(HttpUtil.urlConnectionByRsa("http://127.0.0.1:9999/api/user/info", jsonObject.toJSONString()), new TypeReference<ApiResponse<User>>() {
                            });
                            if ("SUCCESS".equals(userResponse.getCode())) {
                                roomNo = intoRequest.getRoomNo();
                                MahjongTcpService.userClients.put(userId, this);

                                Mahjong.MahjongIntoResponse.Builder intoResponseBuilder = Mahjong.MahjongIntoResponse.newBuilder();
                                if (redisService.exists("room" + roomNo)) {
                                    while (!redisService.lock("lock_room" + roomNo)) {
                                    }

                                    redisService.addCache("reconnect" + userId, "ruijin_mahjong," + roomNo);

                                    Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);
                                    //房间是否已存在当前用户，存在则为重连
                                    final boolean[] find = {false};
                                    room.getSeats().stream().filter(seat -> seat.getUserId() == userId).forEach(seat -> find[0] = true);
                                    if (!find[0]) {
                                        if (room.getCount() > room.getSeats().size()) {
                                            room.addSeat(userResponse.getData());
                                        } else {
                                            roomCardIntoResponseBuilder.setError(GameBase.ErrorCode.COUNT_FULL);
                                            response.setOperationType(GameBase.OperationType.CONNECTION).setData(roomCardIntoResponseBuilder.build().toByteString());
                                            send(response.build(), userId);
                                            redisService.unlock("lock_room" + roomNo);
                                            break;
                                        }
                                    }
                                    intoResponseBuilder.setRoomNo(roomNo);
                                    intoResponseBuilder.setBaseScore(room.getBaseScore());
                                    intoResponseBuilder.setCount(room.getCount());
                                    intoResponseBuilder.setGameTimes(room.getGameTimes());
                                    roomCardIntoResponseBuilder.setError(GameBase.ErrorCode.SUCCESS);
                                    roomCardIntoResponseBuilder.setData(intoResponseBuilder.build().toByteString());
                                    response.setOperationType(GameBase.OperationType.ROOM_INFO).setData(roomCardIntoResponseBuilder.build().toByteString());
                                    send(response.build(), userId);

                                    GameBase.RoomSeatsInfo.Builder roomSeatsInfo = GameBase.RoomSeatsInfo.newBuilder();
                                    for (Seat seat1 : room.getSeats()) {
                                        seat1.setRobot(false);
                                        GameBase.SeatResponse.Builder seatResponse = GameBase.SeatResponse.newBuilder();
                                        seatResponse.setSeatNo(seat1.getSeatNo());
                                        seatResponse.setID(seat1.getUserId());
                                        seatResponse.setScore(seat1.getScore());
                                        seatResponse.setIsReady(seat1.isReady());
                                        seatResponse.setAreaString(seat1.getAreaString());
                                        roomSeatsInfo.addSeats(seatResponse.build());
                                    }
                                    response.setOperationType(GameBase.OperationType.SEAT_INFO).setData(roomSeatsInfo.build().toByteString());
                                    for (Seat seat : room.getSeats()) {
                                        if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                                            MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                                        }
                                    }
                                    if (0 != room.getGameStatus().compareTo(GameStatus.WAITING)) {

                                        for (Seat seat : room.getSeats()) {
                                            if (seat.getSeatNo() == room.getOperationSeatNo()) {
                                                GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder().setID(seat.getUserId()).build();
                                                response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());
                                                send(response.build(), userId);
                                                break;
                                            }
                                        }

                                        Ruijin.RuijinMahjongGameInfo.Builder ruijinMahjongGameInfo = Ruijin.RuijinMahjongGameInfo.newBuilder();
                                        Mahjong.MahjongGameInfo.Builder gameInfo = Mahjong.MahjongGameInfo.newBuilder().setGameStatus(GameBase.GameStatus.PLAYING);
                                        Seat operationSeat = null;
                                        for (Seat seat : room.getSeats()) {
                                            if (seat.getSeatNo() == room.getOperationSeatNo()) {
                                                operationSeat = seat;
                                                break;
                                            }
                                        }
                                        gameInfo.setOperationUser(operationSeat.getUserId());
                                        gameInfo.setLastOperationUser(room.getLastOperation());
                                        for (Seat seat1 : room.getSeats()) {
                                            Mahjong.MahjongSeatGameInfo.Builder seatResponse = Mahjong.MahjongSeatGameInfo.newBuilder();
                                            seatResponse.setID(seat1.getUserId());
                                            seatResponse.setIsRobot(seat1.isRobot());
                                            if (null != seat1.getInitialCards()) {
                                                if (seat1.getUserId() == userId) {
                                                    seatResponse.addAllInitialCards(seat1.getInitialCards());
                                                }
                                            }
                                            if (null != seat1.getCards()) {
                                                if (seat1.getUserId() == userId) {
                                                    seatResponse.addAllCards(seat1.getCards());
                                                } else {
                                                    seatResponse.setCardsSize(seat1.getCards().size());
                                                }
                                            }

                                            if (null != seat1.getPengCards()) {
                                                seatResponse.addAllPengCards(seat1.getPengCards());
                                            }
                                            if (null != seat1.getGangCards()) {
                                                seatResponse.addAllGangCards(seat1.getGangCards());
                                            }
                                            if (null != seat1.getChiCards()) {
                                                seatResponse.addAllChiCards(seat1.getChiCards());
                                            }

                                            if (null != seat1.getPlayedCards()) {
                                                seatResponse.addAllPlayedCards(seat1.getPlayedCards());
                                            }
                                            gameInfo.addSeats(seatResponse.build());
                                        }
                                        ruijinMahjongGameInfo.setMahjongGameInfo(gameInfo);
                                        ruijinMahjongGameInfo.setRogue(room.getJiabao());
                                        response.setOperationType(GameBase.OperationType.GAME_INFO).setData(ruijinMahjongGameInfo.build().toByteString());
                                        send(response.build(), userId);

                                        //才开始的时候检测是否该当前玩家出牌
                                        if (0 == room.getHistoryList().size()) {
                                            for (Seat seat : room.getSeats()) {
                                                if (seat.getSeatNo() == room.getOperationSeatNo() && seat.getUserId() == userId) {
                                                    room.checkSelfGetCard(response, operationSeat);
                                                    break;
                                                }
                                            }
                                        } else if (room.getHistoryList().size() > 0) {
                                            OperationHistory operationHistory = room.getHistoryList().get(room.getHistoryList().size() - 1);
                                            switch (operationHistory.getHistoryType()) {
                                                case GET_CARD:
                                                    if (operationHistory.getUserId() == userId) {
                                                        for (Seat seat : room.getSeats()) {
                                                            if (seat.getUserId() == userId) {
                                                                room.checkSelfGetCard(response, seat);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case PLAY_CARD:
                                                    if (operationHistory.getUserId() != userId) {
                                                        room.checkSeatCan(operationHistory.getCards().get(0), response, userId);
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                    redisService.addCache("room" + roomNo, JSON.toJSONString(room));
                                    redisService.unlock("lock_room" + roomNo);
                                } else {
                                    roomCardIntoResponseBuilder.setError(GameBase.ErrorCode.ROOM_NOT_EXIST);
                                    response.setOperationType(GameBase.OperationType.CONNECTION).setData(roomCardIntoResponseBuilder.build().toByteString());
                                    send(response.build(), userId);
                                }
                            } else {
                                roomCardIntoResponseBuilder.setError(GameBase.ErrorCode.ROOM_NOT_EXIST);
                                response.setOperationType(GameBase.OperationType.CONNECTION).setData(roomCardIntoResponseBuilder.build().toByteString());
                                send(response.build(), userId);
                            }
                            break;
                        case READY:
                            if (redisService.exists("room" + roomNo)) {
                                while (!redisService.lock("lock_room" + roomNo)) {
                                }
                                Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);
                                if (0 == room.getGameStatus().compareTo(GameStatus.READYING) || 0 == room.getGameStatus().compareTo(GameStatus.WAITING)) {
                                    room.getSeats().stream().filter(seat -> seat.getUserId() == userId && !seat.isReady()).forEach(seat -> {
                                        seat.setReady(true);
                                        response.setOperationType(GameBase.OperationType.READY).setData(GameBase.ReadyResponse.newBuilder().setID(userId).build().toByteString());
                                        room.getSeats().stream().filter(seat1 -> MahjongTcpService.userClients.containsKey(seat1.getUserId())).forEach(seat1 ->
                                                MahjongTcpService.userClients.get(seat1.getUserId()).send(response.build(), seat1.getUserId()));
                                    });
                                    boolean allReady = true;
                                    for (Seat seat : room.getSeats()) {
                                        if (!seat.isReady()) {
                                            allReady = false;
                                            break;
                                        }
                                    }
                                    if (allReady && room.getCount() == room.getSeats().size()) {
                                        room.setGameCount(room.getGameCount() + 1);
                                        room.setGameStatus(GameStatus.PLAYING);
                                        room.dealCard();
                                        //骰子
                                        int dice1 = new Random().nextInt(6) + 1;
                                        int dice2 = new Random().nextInt(6) + 1;
                                        room.setDice(new Integer[]{dice1, dice2});
                                        Mahjong.MahjongStartResponse.Builder dealCard = Mahjong.MahjongStartResponse.newBuilder();
                                        Ruijin.RuijinStartResponse.Builder ruijinDealCard = Ruijin.RuijinStartResponse.newBuilder();
                                        ruijinDealCard.setRogue(room.getJiabao());
                                        dealCard.setBanker(room.getBanker()).addDice(dice1).addDice(dice2);
                                        ruijinDealCard.setRogue(room.getJiabao());
                                        response.setOperationType(GameBase.OperationType.START);
                                        room.getSeats().stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId())).forEach(seat -> {
                                            dealCard.clearCards();
                                            dealCard.addAllCards(seat.getCards());
                                            ruijinDealCard.setMahjongStartResponse(dealCard);
                                            response.setData(ruijinDealCard.build().toByteString());
                                            MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId());
                                        });

                                        GameBase.RoundResponse roundResponse = GameBase.RoundResponse.newBuilder().setID(room.getBanker()).build();
                                        response.setOperationType(GameBase.OperationType.ROUND).setData(roundResponse.toByteString());
                                        room.getSeats().stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId()))
                                                .forEach(seat -> MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId()));

                                        Seat operationSeat = null;
                                        for (Seat seat : room.getSeats()) {
                                            if (room.getOperationSeatNo() == seat.getSeatNo()) {
                                                operationSeat = seat;
                                                break;
                                            }
                                        }

                                        room.checkSelfGetCard(response, operationSeat);

                                    }
                                }

                                redisService.addCache("room" + roomNo, JSON.toJSONString(room));
                                redisService.unlock("lock_room" + roomNo);
                            } else {
                                logger.warn("房间不存在");
                            }
                            break;
                        case COMPLETED:
                            if (redisService.exists("room" + roomNo)) {
                                while (!redisService.lock("lock_room" + roomNo)) {
                                }
                                Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);
                                room.getSeats().stream().filter(seat -> seat.getUserId() == userId && !seat.isCompleted())
                                        .forEach(seat -> seat.setCompleted(true));
                                boolean allCompleted = true;
                                for (Seat seat : room.getSeats()) {
                                    if (!seat.isCompleted()) {
                                        allCompleted = false;
                                        break;
                                    }
                                }
                                if (allCompleted) {
                                    //TODO 出牌超时
                                }
                                redisService.addCache("room" + roomNo, JSON.toJSONString(room));
                                redisService.unlock("lock_room" + roomNo);
                            } else {
                                logger.warn("房间不存在");
                            }
                            break;
                        case ACTION:
                            GameBase.BaseAction actionRequest = GameBase.BaseAction.parseFrom(request.getData());
                            logger.info(userId + "Action\n" + actionRequest + "\n");
                            GameBase.BaseAction.Builder actionResponse = GameBase.BaseAction.newBuilder().setID(userId);
                            if (redisService.exists("room" + roomNo)) {
                                while (!redisService.lock("lock_room" + roomNo)) {
                                }
                                Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);
                                switch (actionRequest.getOperationId()) {
                                    case PLAY_CARD:
                                        Mahjong.MahjongPlayCard playCardRequest = Mahjong.MahjongPlayCard.parseFrom(actionRequest.getData());
                                        final boolean[] shouldOperation = {false};
                                        final Seat[] operationSeat = new Seat[1];
                                        room.getSeats().stream().filter(seat -> seat.getUserId() == userId).forEach(seat -> {
                                            if (room.getOperationSeatNo() == seat.getSeatNo()) {
                                                shouldOperation[0] = true;
                                                operationSeat[0] = seat;
                                            } else {
                                                logger.warn("不该当前玩家操作" + userId);
                                            }
                                        });
                                        if (!shouldOperation[0]) {
                                            break;
                                        }
                                        Integer card = playCardRequest.getCard();
                                        if (operationSeat[0].getCards().contains(card)) {
                                            operationSeat[0].getCards().remove(card);
                                            if (null == operationSeat[0].getPlayedCards()) {
                                                operationSeat[0].setPlayedCards(new ArrayList<>());
                                            }
                                            operationSeat[0].getPlayedCards().add(card);
                                            Mahjong.MahjongPlayCard.Builder builder = Mahjong.MahjongPlayCard.newBuilder().setCard(playCardRequest.getCard());

                                            actionResponse.setOperationId(GameBase.ActionId.PLAY_CARD).setData(builder.build().toByteString());

                                            response.setOperationType(GameBase.OperationType.ACTION).setData(actionResponse.build().toByteString());
                                            room.setLastOperation(userId);
                                            room.getHistoryList().add(new OperationHistory(userId, OperationHistoryType.PLAY_CARD, card));
                                            room.getSeats().stream().filter(seat -> MahjongTcpService.userClients.containsKey(seat.getUserId()))
                                                    .forEach(seat -> MahjongTcpService.userClients.get(seat.getUserId()).send(response.build(), seat.getUserId()));
                                            //先检查其它三家牌，是否有人能胡、杠、碰
                                            room.checkCard(card, response, redisService);
                                        } else {
                                            logger.warn("用户手中没有此牌" + userId);
                                        }

                                        break;
                                    case PENG:
                                        room.getSeats().stream().filter(seat -> seat.getUserId() == userId &&
                                                room.getOperationSeatNo() != seat.getSeatNo()).forEach(seat -> seat.setOperation(3));
                                        if (room.checkCanPeng()) { //如果可以碰、杠牌，则碰、杠
                                            room.operation(actionResponse, response, redisService);
                                        }
                                        break;
                                    case AN_GANG:
                                    case BA_GANG:
                                        Mahjong.MahjongGang gangRequest = Mahjong.MahjongGang.parseFrom(actionRequest.getData());
                                        room.selfGang(actionResponse, gangRequest.getCard(), response, redisService, userId);
                                        break;
                                    case DIAN_GANG:
                                        room.getSeats().stream().filter(seat -> seat.getUserId() == userId &&
                                                room.getOperationSeatNo() != seat.getSeatNo()).forEach(seat -> seat.setOperation(2));
                                        if (room.checkCanPeng()) { //如果可以碰、杠牌，则碰、杠
                                            room.operation(actionResponse, response, redisService);
                                        }
                                        break;
                                    case CHI:
                                        Mahjong.MahjongChi chiRequest = Mahjong.MahjongChi.parseFrom(actionRequest.getData());
                                        if (0 < room.getHistoryList().size()) {
                                            OperationHistory operationHistory = room.getHistoryList().get(room.getHistoryList().size() - 1);
                                            if (0 == operationHistory.getHistoryType().compareTo(OperationHistoryType.PLAY_CARD)) {
                                                room.getSeats().stream().filter(seat -> seat.getUserId() == userId &&
                                                        room.getOperationSeatNo() != seat.getSeatNo()).forEach(seat -> {
                                                    List<Integer> cards = new ArrayList<>();
                                                    cards.addAll(chiRequest.getCardsList());
                                                    Card.remove(cards, operationHistory.getCards().get(0));
                                                    if (Card.containAll(seat.getCards(), cards)) {
                                                        seat.setChiTemp(cards);
                                                    }
                                                    if (MahjongUtil.checkChi(cards, operationHistory.getCards().get(0), room.getJiabao())) {
                                                        if (room.checkCanChi()) { //如果可以吃
                                                            room.operation(actionResponse, response, redisService);
                                                            return;
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        break;
                                    case HU:
                                        room.getSeats().stream().filter(seat -> seat.getUserId() == userId &&
                                                room.getOperationSeatNo() != seat.getSeatNo()).forEach(seat -> seat.setOperation(1));
                                        room.hu(userId, response, redisService);//胡
                                        break;
                                    case PASS:
                                        room.getSeats().stream().filter(seat -> seat.getUserId() == userId &&
                                                room.getOperationSeatNo() != seat.getSeatNo()).forEach(seat -> {
                                            seat.setOperation(4);
                                            if (!room.passedChecked()) {//如果都操作完了，继续摸牌
                                                room.getSeats().forEach(seat1 -> {
                                                    seat1.setOperation(0);
                                                    seat1.getChiTemp().clear();
                                                });
                                                room.getCard(response, room.getNextSeat(), redisService);
                                            } else if (room.checkCanPeng()) { //如果可以碰、杠牌，则碰、杠
                                                room.operation(actionResponse, response, redisService);
                                            }
                                        });
                                        break;

                                }
                                if (null != room.getRoomNo()) {
                                    redisService.addCache("room" + roomNo, JSON.toJSONString(room));
                                }
                                redisService.unlock("lock_room" + roomNo);
                            } else {
                                logger.warn("房间不存在");
                            }
                            break;
                        case REPLAY:
                            Ruijin.RuijinMahjongReplayResponse.Builder replayResponse = Ruijin.RuijinMahjongReplayResponse.newBuilder();
                            if (redisService.exists("room" + roomNo)) {
                                while (!redisService.lock("lock_room" + roomNo)) {
                                }
                                Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);

                                Mahjong.MahjongStartResponse.Builder dealCard = Mahjong.MahjongStartResponse.newBuilder();
                                Ruijin.RuijinStartResponse.Builder ruijinDealCard = Ruijin.RuijinStartResponse.newBuilder();
                                dealCard.setBanker(room.getBanker()).addAllDice(Arrays.asList(room.getDice()));
                                ruijinDealCard.setRogue(room.getJiabao());
                                replayResponse.setStart(ruijinDealCard);

                                for (OperationHistory operationHistory : room.getHistoryList()) {
                                    GameBase.OperationHistory.Builder builder = GameBase.OperationHistory.newBuilder();
                                    builder.setID(operationHistory.getUserId());
                                    builder.addAllCard(operationHistory.getCards());
                                    switch (operationHistory.getHistoryType()) {
                                        case GET_CARD:
                                            builder.setOperationId(GameBase.ActionId.GET_CARD);
                                            break;
                                        case PLAY_CARD:
                                            builder.setOperationId(GameBase.ActionId.PLAY_CARD);
                                            break;
                                        case PENG:
                                            builder.setOperationId(GameBase.ActionId.PENG);
                                            break;
                                        case AN_GANG:
                                            builder.setOperationId(GameBase.ActionId.AN_GANG);
                                            break;
                                        case DIAN_GANG:
                                            builder.setOperationId(GameBase.ActionId.DIAN_GANG);
                                            break;
                                        case BA_GANG:
                                            builder.setOperationId(GameBase.ActionId.BA_GANG);
                                            break;
                                        case HU:
                                            builder.setOperationId(GameBase.ActionId.HU);
                                            break;
                                    }
                                    replayResponse.addHistory(builder);
                                }
                                response.setOperationType(GameBase.OperationType.REPLAY).setData(replayResponse.build().toByteString());
                                send(response.build(), userId);
                                redisService.unlock("lock_room" + roomNo);
                            }
                            break;
                        case EXIT:
                            break;
                        case DISSOLVE:
                            if (redisService.exists("room" + roomNo)) {
                                while (!redisService.lock("lock_room" + roomNo)) {
                                }
                                Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);
                                response.setOperationType(GameBase.OperationType.DISSOLVE).clearData();
                                for (Seat seat : room.getSeats()) {
                                    if (MahjongTcpService.userClients.containsKey(seat.getUserId())) {
                                        send(response.build(), seat.getUserId());
                                    }
                                }
                                room.roomOver(response, redisService);
                                redisService.unlock("lock_room" + roomNo);
                            }
                            break;
                    }
                }
            }
        } catch (EOFException e) {
            logger.info("socket.shutdown.message");
            close();
        } catch (IOException e) {
            logger.info("socket.dirty.shutdown.message" + e.getMessage());
            close();
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("socket.dirty.shutdown.message");
            close();
            e.printStackTrace();
        }
    }
}