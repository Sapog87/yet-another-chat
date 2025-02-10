package org.sber.yetanotherchat.service;

import org.sber.yetanotherchat.dto.message.StatusOutputMessage;

import java.security.Principal;
import java.util.List;

public interface WebSocketUserService {
    List<StatusOutputMessage> getStatuses(Principal sender);
}
