package app.todoit.domain.friend.service;

import app.todoit.domain.auth.entity.User;
import app.todoit.domain.auth.repository.UserRepository;
import app.todoit.domain.friend.dto.FriendResponseDto;
import app.todoit.domain.friend.dto.JoinCheckDto;
import app.todoit.domain.friend.dto.PendingFriendResponseDto;
import app.todoit.domain.friend.entity.FriendEntity;
import app.todoit.domain.friend.entity.FriendId;
import app.todoit.domain.friend.entity.PendingFriendEntity;
import app.todoit.domain.friend.entity.PendingFriendId;
import app.todoit.domain.friend.repository.FriendRepository;
import app.todoit.domain.friend.repository.PendingFriendRepository;
import app.todoit.global.exception.ApiException;
import app.todoit.global.exception.ErrorCode;
import app.todoit.global.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendService {
    private final PendingFriendRepository pendingFriendRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    public String addFriend (User user, Long friendId) {
        //친구 신청 & 취소
        User friend= getUserEntity(friendId);

        PendingFriendEntity p = new PendingFriendEntity(user, friend);
        if (friendRepository.existsById(new FriendId(user.getId(), friendId)) || friendRepository.existsById(new FriendId(friendId,user.getId()))) {
            throw new ApiException(ErrorCode.ALREADY_FRIENDS);
        }
        else if (pendingFriendRepository.existsById(p.getPendingFriendId())) { //이미 신청한 상태면 신청 취소
            pendingFriendRepository.deleteById(p.getPendingFriendId());
            return "친구 신청 취소 성공";
        }
        else {
            pendingFriendRepository.save(p);
            return "친구 신청 성공";
        }
    }

    public String acceptFriend (User user, Long friendId) {
        //pending Friend 와 Friend 의 user_id, friend_id 방향 같게 저장
        //친구 수락 (pendingFriends 에서 삭제하고 friend에 추가)
        User friend= getUserEntity(friendId);
        if (!pendingFriendRepository.existsById(new PendingFriendId(friendId, user.getId()))) {
            throw new ApiException(ErrorCode.CANNOT_ACCEPT); //친구가 신청을 취소했는데 수락을 누른경우
        }
        pendingFriendRepository.deleteById(new PendingFriendId(friendId,user.getId()));
        friendRepository.save(new FriendEntity(friend, user));

        return "친구 신청 수락 성공";

    }

    public String deleteFriend (User user, Long friendId) {
        //친구 삭제
        Integer friend = friendRepository.exists(user.getId(), friendId);
        if (!friend.equals(1)){
            throw new NotFoundException((ErrorCode.FRIENDS_NOT_FOUND));
        }
        else {
            friendRepository.deleteFriend(user.getId(), friendId);
            return "친구 삭제 성공";
            }
    }

    public PendingFriendResponseDto getPendingFriends (User user) {
        //내가 친구 신청한 목록
        PendingFriendResponseDto res = new PendingFriendResponseDto();
        res.entityToDtoByMe( pendingFriendRepository.findAllByPendingFriendIdUserId(user.getId()));
        return res;

    }

    public FriendResponseDto getFriendsList (User user) {
        //친구 목록 조회 (양방향인 경우)
        FriendResponseDto res = new FriendResponseDto();

        res.entityToDtoByMe(friendRepository.findByUserId(user.getId())); //내가 건 친구
        res.entityToDtoByFriend(friendRepository.findByFriendIdFriendId(user.getId())); //내가 수락한 친구

        return res;
    }

    public JoinCheckDto checkJoinByPhone (User user, List<String> phone) {
        List<User> userEntity = new ArrayList<>();
        for (String s : phone) {
            Optional<User> join = userRepository.findByPhone(s);
            if (join.isPresent()) {
                if (friendRepository.exists(user.getId(),join.get().getId())==0 &&
                pendingFriendRepository.exists(user.getId(),join.get().getId())==0) {
                    userEntity.add(join.get());
                }
            }
        }
        return new JoinCheckDto(userEntity);
    }

    public PendingFriendResponseDto getNeedAcceptFriendList (User user) { //나한테 온 친구신청 목록 조회
        PendingFriendResponseDto res = new PendingFriendResponseDto();
        res.entityToDtoByOthers( pendingFriendRepository.findAllByPendingFriendIdFriendId(user.getId()));
        return res;
    }

    public User getUserEntity(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }
}
