package mahjong.timeout;

import com.alibaba.fastjson.JSON;
import mahjong.mode.GameBase;
import mahjong.mode.Room;
import mahjong.redis.RedisService;

/**
 * Created by pengyi
 * Date : 17-8-31.
 * desc:
 */
public class OperationTimeout extends Thread {

    private int userId;
    private String roomNo;
    private int operationCount;
    private int gameCount;
    private RedisService redisService;
    private boolean hu;
    private GameBase.BaseConnection.Builder response;

    public OperationTimeout(int userId, String roomNo, int operationCount, int gameCount, RedisService redisService, boolean hu) {
        this.userId = userId;
        this.roomNo = roomNo;
        this.operationCount = operationCount;
        this.gameCount = gameCount;
        this.redisService = redisService;
        this.hu = hu;
        this.response = GameBase.BaseConnection.newBuilder();
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                wait(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (redisService.exists("room" + roomNo)) {
            while (!redisService.lock("lock_room" + roomNo)) {
            }
            Room room = JSON.parseObject(redisService.getCache("room" + roomNo), Room.class);

            if (room.getGameCount() == gameCount && room.getHistoryList().size() == operationCount) {
                if (hu) {
                    room.getSeats().stream().filter(seat -> seat.getUserId() == userId).forEach(seat -> seat.setOperation(1));
                    room.hu(userId, response, redisService);//胡
                } else {
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
                            room.operation(GameBase.BaseAction.newBuilder(), response, redisService);
                        }
                    });
                }
            }
            if (null != room.getRoomNo()) {
                redisService.addCache("room" + roomNo, JSON.toJSONString(room));
            }
            redisService.unlock("lock_room" + roomNo);
        }
    }
}
