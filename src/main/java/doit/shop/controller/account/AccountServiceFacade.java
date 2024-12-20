package doit.shop.controller.account;

import doit.shop.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountServiceFacade {

    private final AccountService accountService;

    public void depositAccount(Long accountId, Integer amount) throws InterruptedException {

        //비관적 락
        accountService.depositAccount(accountId,amount);

        // 낙관적 락
//        while (true) {
//            try {
//                accountService.depositAccount(accountId,amount);
//                break;
//            } catch (OptimisticLockingFailureException e) {
//                Thread.sleep(50);
//            }
//        }
    }

    public void withdrawAccount(Long accountId, Integer amount) throws InterruptedException {
        accountService.withdrawAccount(accountId,amount);
//        while (true) {
//            try {
//                accountService.withdrawAccount(accountId,amount);
//                break;
//            } catch (OptimisticLockingFailureException e) {
//                Thread.sleep(50);
//            }
//        }
    }
}
