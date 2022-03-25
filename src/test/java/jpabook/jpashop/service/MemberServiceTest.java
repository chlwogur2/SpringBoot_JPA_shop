package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Kim");
        //when
        Long saveId = memberService.join(member);
        //then
        assertEquals(member, memberService.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("Choi");

        Member member2 = new Member();
        member2.setName("Choi");
        //when
        memberService.join(member1);
        memberService.join(member2);
        //then
        fail("예외가 떠야하는데 어째서...?");
    }

    @Test
    public void findMembers() throws Exception{

        //given

        //when

        //then
    }

    @Test
    public void findOne() throws Exception{

        //given

        //when

        //then
    }
}