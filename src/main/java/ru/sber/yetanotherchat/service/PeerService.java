package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.PeerDto;

import java.security.Principal;
import java.util.List;

/**
 *
 */
public interface PeerService {
    /**
     * @param principal
     * @return
     */
    List<PeerDto> getAllChats(Principal principal);
}
