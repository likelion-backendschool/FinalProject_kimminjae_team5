package com.example.withdraw;

import com.example.member.MemberDto;
import com.example.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/withdraw")
public class WithdrawController {
    private final WithdrawService withdrawService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String withdrawList(Principal principal, Model model) {
        MemberDto memberDto = memberService.getMemberByUsername(principal.getName());
        List<Withdraw> withdrawList = withdrawService.getByMember(memberDto);

        model.addAttribute("withdrawList", withdrawList);
        return "withdraw/list";
    }

    @GetMapping("/apply")
    public String requestWithdraw(Principal principal, Model model) {
        MemberDto memberDto = memberService.getMemberByUsername(principal.getName());

        model.addAttribute("member", memberDto);
        return "/withdraw/apply_form";
    }
    @PostMapping("/apply")
    public String requestWithdraw(Principal principal,
                                  @RequestParam("bankName") String bankName,
                                  @RequestParam("bankAccountNo") String bankAccountNo,
                                  @RequestParam("price") long price) {
        MemberDto memberDto = memberService.getMemberByUsername(principal.getName());
        Withdraw withdraw = withdrawService.create(bankName, bankAccountNo, price, memberDto);
        memberService.minusRestCash(withdraw.getPrice(), withdraw.getMember());

        return "redirect:/withdraw/list";
    }
    @PostMapping("/cancel/{id}")
    public String cancelWithdraw(Principal principal, @PathVariable("id") long id) {
        Withdraw withdraw = withdrawService.getById(id);
        withdrawService.cancel(withdraw);
        memberService.plusRestCash(withdraw.getPrice(), withdraw.getMember());

        return "redirect:/withdraw/list";
    }
}