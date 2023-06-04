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


/*
Lettuce
구현이 간단하다
spring data redis 를 이용하면 lettuce 가 기본이기때문에 별도의 라이브러리를 사용하지 않아도 된다.
spin lock 방식이기때문에 동시에 많은 스레드가 lock 획득 대기 상태라면 redis 에 부하가 갈 수 있다.
리
Redisson
락 획득 재시도를 기본으로 제공한다.
pub-sub 방식으로 구현이 되어있기 때문에 lettuce 와 비교했을 때 redis 에 부하가 덜 간다.
별도의 라이브러리를 사용해야한다.
lock 을 라이브러리 차원에서 제공해주기 떄문에 사용법을 공부해야 한다.


실무에서는 ?
재시도가 필요하지 않은 lock 은 lettuce 활용
재시도가 필요한 경우에는 redisson 를 활용
* */

/*
Mysql
이미 Mysql 을 사용하고 있다면 별도의 비용없이 사용가능하다.
어느정도의 트래픽까지는 문제없이 활용이 가능하다.
Redis 보다는 성능이 좋지않다.

Redis
활용중인 Redis 가 없다면 별도의 구축비용과 인프라 관리비용이 발생한다.
Mysql 보다 성능이 좋다.

* */