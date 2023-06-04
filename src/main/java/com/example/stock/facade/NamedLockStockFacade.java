package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.OptimisticLockStockService;
import com.example.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decreaseNamed(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}


/* Named Lock
 * 이름을 가진 메타 데이터 락
 * 이름을 가진 락을 획득한 후 해제될 떄 까지 다른 세션은 이 락을 획득할 수 없음
 * 주의할 점은 트랜젝션이 끝날 떄 락이 자동으로 해제되지 않음으로 별도의 명령으로 해제해주거나 시간이 지나야함
 * PessimisticLock은 스탁에 락을 걸었지만, Named는 별도의 공간에 락을 걸어줌
 * PessimisticLock은 타임 아웃을 구현하기 힘들지만 네임드 락은 쉬움
 * 주로 분산락을 구현할 때, 삽입시 데이터 정합성을 맞춰야 할 때 사용
 * 트랜젝션 종료시 락 해제와 세션 관리를 잘 해야함
 * */
