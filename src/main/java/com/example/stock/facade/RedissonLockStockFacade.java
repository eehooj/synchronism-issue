package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {

    private RedissonClient redissonClient;

    private StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString());

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS); // 몇 초 동안 기다릴 건지, 얼마나 점유할 건지

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decreaseRedis(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}

/* Redisson
- 레디슨은 자신이 점유하고 있는 락을 해제할 때 채널에 락획득을 해야 하는 스레드들에게 락 획득을 하라고 메시지를 보냄
- 계속해서 락 요청을 하는게 아니라 락 해제 메시지가 들어왔을 때 한 두 번만 요청을 하기 때문에 레투스보다 부하가 적음
- 락 관련 클래스를 제공해 주기 때문에 별도의 리파지토리를 만들지 않아도 됨
- 레투스에 비해 설정이 복잡하고 별도의 라이브러리를 사용해야함
* */