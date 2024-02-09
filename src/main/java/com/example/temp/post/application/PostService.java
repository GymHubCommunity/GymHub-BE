package com.example.temp.post.application;

import static com.example.temp.common.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostRepository;
import com.example.temp.post.dto.response.PagePostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    public PagePostResponse findPostsFromFollowings(UserContext userContext, Pageable pageable) {
        Member member = memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
        List<Member> followingMembers = findFollowingMemberFrom(member);
        Page<Post> posts = postRepository.findByMemberInOrderByCreatedAtDesc(followingMembers, pageable);
        return PagePostResponse.from(posts);
    }

    private List<Member> findFollowingMemberFrom(Member member) {
        return followRepository.findAllByFromIdAndStatus(
                member.getId(), FollowStatus.APPROVED).stream()
            .map(Follow::getTo)
            .toList();
    }
}
