package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private RedisLockRepository redisLockRepository;

    private StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            stockService.decreaseRedis(key, quantity);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}

/* Lettuce
* 구현이 간단함
* 스핀락방식으로 레디스에 부하를 줄 수 있음
* 떄문에 스레드 슬립을 사용하여 획득 재시도시 텀을 줘야 함
* */