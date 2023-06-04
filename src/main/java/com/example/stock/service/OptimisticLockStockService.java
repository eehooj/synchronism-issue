package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}

/* Optimistic Lock
* 실제 락을 이용하지 않고 버전을 이용하여 데이터의 정합성을 맞춤
* 별도의 락을 사용하지 않기 때문에 퍼시미스틱 락 보다 성능상 이점이 있음
* 업데이트시 에러 발생시 재시도 로직을 개발자가 만들어야 함
* 충돌이 빈번하게 일어난다면 퍼시미스틱 락을 사용하는 것이 더 좋음
* */