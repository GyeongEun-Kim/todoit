package app.todoit.domain.friend.service;

import app.todoit.auth.entity.User;
import app.todoit.auth.repository.UserRepository;
import app.todoit.domain.friend.dto.FriendResponseDto;
import app.todoit.domain.friend.entity.FriendEntity;
import app.todoit.domain.friend.entity.FriendId;
import app.todoit.domain.friend.entity.PendingFriendEntity;
import app.todoit.domain.friend.entity.PendingFriendId;
import app.todoit.domain.friend.exception.FriendException;
import app.todoit.domain.friend.repository.FriendRepository;
import app.todoit.domain.friend.repository.PendingFriendRepository;
import app.todoit.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendService {
    private final PendingFriendRepository pendingFriendRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    public String addFriend (Long userId, Long friendId) {
        //친구 신청 & 취소
        User user = getUserEntity(userId);
        User friend= getUserEntity(friendId);
        PendingFriendEntity p = new PendingFriendEntity(user, friend);
        if (friendRepository.existsById(new FriendId(userId,friendId)) || friendRepository.existsById(new FriendId(friendId,userId))) {
            throw new FriendException(ErrorCode.ALREADY_FRIENDS);
        }
        else if (pendingFriendRepository.existsById(p.getPendingFriendId())) { //이미 신청한 상태면 신청 취소
            pendingFriendRepository.deleteById(p.getPendingFriendId());
            return "CANCEL SUCCESS";
        }
        else {
            pendingFriendRepository.save(p);
            return "ADD SUCCESS";
        }
    }

    public String acceptFriend (Long userId, Long friendId) {
        //pending Friend 와 Friend 의 user_id, friend_id 방향 같게 저장
        //친구 수락 (pendingFriends 에서 삭제하고 friend에 추가)
        User user = getUserEntity(userId);
        User friend= getUserEntity(friendId);
        pendingFriendRepository.deleteById(new PendingFriendId(friendId,userId));
        friendRepository.save(new FriendEntity(friend, user));

        return "SUCCESS";

    }

    public String deleteFriend (Long userId, Long friendId) {
        //친구 삭제

        Integer isDeleted = friendRepository.deleteFriend(userId, friendId);
        if (isDeleted==0) {
            throw new FriendException(ErrorCode.FRIENDS_NOT_FOUND);
        }
        else {
            return "SUCCESS";
        }
    }

    public FriendResponseDto getPendingFriends (Long userId) {
        //수락 대기 목록 조회
        FriendResponseDto res = new FriendResponseDto();
        res.entityToDto( pendingFriendRepository.findAllByPendingFriendIdFriendId(userId));
        return res;

    }

    public FriendResponseDto getFriendsList (Long userId) {
        //친구 목록 조회 (양방향인 경우)
        FriendResponseDto res = new FriendResponseDto();
        res.entityToDto(friendRepository.findMyFriends(userId));
        return res;


    }

    public User getUserEntity(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }
}
