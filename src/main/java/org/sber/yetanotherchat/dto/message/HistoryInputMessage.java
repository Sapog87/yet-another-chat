package org.sber.yetanotherchat.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class HistoryInputMessage {
    @NotNull
    private Long peerId;
    private Long offsetId;
    @Range(min = 0, max = Integer.MAX_VALUE)
    private Integer limit;
}