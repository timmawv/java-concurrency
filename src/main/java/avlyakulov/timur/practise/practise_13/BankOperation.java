package avlyakulov.timur.practise.practise_13;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class BankOperation {
    private int operationId;
    private int fromAccountId;
    private int toAccountId;
    private int amount;
    private LocalDateTime createdAt;
}
