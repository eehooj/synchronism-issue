package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class PessimisticLockStockService {

    private StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}

/* PessimisticLock
* 락이 해제 될 때까지 접근 불가
* 충돌이 빈번하게 일어나는 상황이라면 옵티미스틱락보다 성능이 좋음
* 락을 통해 데이터를 보존하기 떄문에 데이터 정합성이 어느정도 보장됨
* 그러나 별도의 락을 잡기 때문에 성능 저하가 있을 수 있음
* */
