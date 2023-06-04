package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    //@Transactional
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseRedis(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 부모의 트랜젝션과 별도로 실행 되어야 함
    public void decreaseNamed(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}


/* synchronized
* - 사용 이유 : 해당 메소드를 한 쓰레드에서만 돌리기 위해 사용
* - 사용해도 테스트 케이스 실패용
* - @Transactional 때문
* - @Transactional은 우리가 만든 클래스를 새로 만들어서 실행 (프록시 객체를 만들어서 실행하는 방식)
* - 때문에 호출을 하면 새로운 프록시 객체가 생성되어 하나의 메소드에 하나의 쓰레드만 동작하게 됨
* - stock.decrease()가 호출 될 때 트랜젝션이 시작하고
* - stock.decrease() 정상적으로 종료될 떄 트랜젝션이 종료
* - 트랜젝션 종료 시점에 디비 업데이트가 일어나는데, 여기서 문제가 발생
* - stock.decrease()가 정상적으로 종료되고 트랜젝션이 종료되기 전 다른 스레드가 stock.decrease()를 호출할 수 있음
* - 그렇게 되면 다른 스레드는 갱신 전 데이터를 가져오기 떄문에 동일한 문제 발생
* - @Transactional을 지우면 정상 동작함!
*
* - 각 프로세스 안에서만 보장이 됨 -> 서버가 여러 대라면? 여러 쓰레드에서 접근이 가능
* - 운영 환경에서는 하나의 서버만 사용하지 않음
* - 그래서 잘 사용하지 않음
* */