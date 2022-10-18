package com.example.post;

import com.example.DataNotFoundException;
import com.example.member.Member;
import com.example.member.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    //글 작성
    public PostDto write(MemberDto member, String subject, String content) {
        Post post = Post.builder()
                .subject(subject)
                .content(content)
                .contentHtml(content)
                .createDate(LocalDateTime.now())
                .member(member.toEntity())
                .build();
        postRepository.save(post);

        return post.toDto();
    }

    //전체 글 불러오기
    public List<PostDto> getAllPost() {
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createDate"));
//
        List<Post> postList = postRepository.findAll();
        List<PostDto> postDtoList = new ArrayList<>();

        for(Post post : postList) {
            postDtoList.add(post.toDto());
        }
//        postDtoList.sort();
        return postDtoList;
    }

    //id로 글 찾기
    public PostDto getPostById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if(optionalPost.isPresent()) {
            return optionalPost.get().toDto();
        } else {
            throw new DataNotFoundException("글을 찾을 수 없습니다.");
        }
    }

    //글 수정
    public void modify(PostDto postDto, String subject, String content) {
        Optional<Post> optionalPost = postRepository.findById(postDto.getId());

        Post post;
        if(optionalPost.isPresent()) {
            post = optionalPost.get();
        } else {
            throw new DataNotFoundException("글이 존재하지 않습니다.");
        }
        post.setSubject(subject);
        post.setContent(content);
        //마크다운 형식으로 변환해 저장
        post.setContentHtml(content);
        postRepository.save(post);
    }

    //글 삭제
    public void delete(PostDto postDto) {
        Optional<Post> optionalPost = postRepository.findById(postDto.getId());
        if(optionalPost.isPresent()) {
            postRepository.delete(optionalPost.get());
        } else {
            throw new DataNotFoundException("글이 존재하지 않습니다.");
        }
    }
}
