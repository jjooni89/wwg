package com.project.wwg.qna.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.wwg.qna.model.Qna;
import com.project.wwg.qna.service.PagingPgm;
import com.project.wwg.qna.service.QnaService;


@Controller
public class QnaController{
	
	@Autowired
	private QnaService qs; //service 인터페이스
	
	//메인페이지
	@RequestMapping("/")
	public String mainpage(Qna qna, Model model) {
	    List<Qna> qnalist = null;
        qnalist = qs.getQnaMain(); 
        
		model.addAttribute("qnalist", qnalist);
		return "qna/main";
	}
	
	
	//글작성폼
	@RequestMapping("qnawriteform.do")
	public String qnaWriteForm() {
		System.out.println("qnawriteform성공");
		return "qna/qnaWrite";
	}
	
	
	//글작성
	@RequestMapping("qnawrite.do")
	public String qnaInsert(Qna qna, Model model) throws Exception {
		System.out.println("qnawrite까지는 왔음");
		//qna_no 증가
//		int num = qna.getQna_no();
//		System.out.println("qna_no 가져옴");
//		int number = qs.getMaxNum();
//		System.out.println("증가시킨 seq number에 할당함");	
//		qna.setQna_no(number);
//		System.out.println("number값을 qna_no에 set함");
		int result = qs.insert(qna);
//		System.out.println("service에 있는 insert사용하겠다고신호OK");
		model.addAttribute("result", result);
		return "qna/qnaWriteResult";
	}
	
	
	//글목록list
	@RequestMapping("qnalist.do")
	public String qnaList(Model model, HttpServletRequest request, Qna qna) throws Exception {
		
		List<Qna> qnalist = new ArrayList<Qna>();

		int page = 1;   //1번게시물부터
		int limit = 10; //10번게시물까지 출력
		
		if(request.getParameter("page") != null) { //요청받은값이 null값이 아니면?
			page = Integer.parseInt(request.getParameter("page")); //형변환
		}
		
		//1.총 리스트 개수
		int listcount = qs.getListCount();
	
		//3.총 페이지 개수
		int maxpage = (int) ((double) listcount / limit + 0.95);
		
		//4.한개의 page 보여줄 시작페이지 수 (1,11,21,31 ...)
		int startpage = ( ((int) ((double) page / 10 + 0.9)) -1 ) * 10 + 1;
		
		//5.한개의 page 보여줄 끝페이지 수 (10,20,30,40 ...)
		int endpage = maxpage;
		
		//끝페이지수가 
		if(endpage > startpage + 10 -1) 
			endpage = startpage + 10 -1;
		
		//게시물번호 설정
		int no = listcount - (page-1) * limit;
		

		final int rowPerPage = 10;
//		if (page == null || page.equals("")) {
//			page = "1";
//		}
		int currentPage = page;
		// int total = bs.getTotal();
		int total = qs.getTotal(qna); // 검색
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;
		PagingPgm pp = new PagingPgm(total, rowPerPage, currentPage);
		qna.setStartRow(startRow);
		qna.setEndRow(endRow);
		
		//2.페이지 번호를를 dao클래스에 전달하기 위해?
		qnalist = qs.getPageList(qna);  //list받아오기위한
		
		System.out.println(qnalist);
		
		
		model.addAttribute("page", page); //현재페이지num
		model.addAttribute("startpage", startpage); //시작페이지num
		model.addAttribute("endpage", endpage); //끝페이지num
		model.addAttribute("maxpage", maxpage); //총 게시물개수
		model.addAttribute("listcount", listcount); //총 리스트
		model.addAttribute("qnalist", qnalist); //list화
		model.addAttribute("qna", qna); 		
		model.addAttribute("no", no);
		model.addAttribute("pp", pp);
		
		model.addAttribute("search", qna.getSearch());
		model.addAttribute("keyword", qna.getKeyword());
		
		System.out.println("list호출");
		return "qna/qnaList";
	}
	
	//qnalist호출
	@RequestMapping("qnalist2.do")
	public String qnaList2(Model model, HttpServletRequest request, Qna qna) throws Exception {
		
		List<Qna> qnalist = new ArrayList<Qna>();

		int page = 1;   //1번게시물부터
		int limit = 10; //10번게시물까지 출력
		
		if(request.getParameter("page") != null) { //요청받은값이 null값이 아니면?
			page = Integer.parseInt(request.getParameter("page")); //형변환
		}
		
		//1.총 리스트 개수
		int listcount = qs.getListCount();
	
		//3.총 페이지 개수
		int maxpage = (int) ((double) listcount / limit + 0.95);
		
		//4.한개의 page 보여줄 시작페이지 수 (1,11,21,31 ...)
		int startpage = ( ((int) ((double) page / 10 + 0.9)) -1 ) * 10 + 1;
		
		//5.한개의 page 보여줄 끝페이지 수 (10,20,30,40 ...)
		int endpage = maxpage;
		
		//끝페이지수가 
		if(endpage > startpage + 10 -1) 
			endpage = startpage + 10 -1;
		
		//게시물번호 설정
		int no = listcount - (page-1) * limit;
		

		final int rowPerPage = 10;
//		if (page == null || page.equals("")) {
//			page = "1";
//		}
		int currentPage = page;
		int total = qs.getTotal(qna); // 검색
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;
		PagingPgm pp = new PagingPgm(total, rowPerPage, currentPage);
		qna.setStartRow(startRow);
		qna.setEndRow(endRow);
		
		//2.페이지 번호를를 dao클래스에 전달하기 위해?
		qnalist = qs.getPageList(qna);  //list받아오기위한
		
		System.out.println(qnalist);
		
		
		model.addAttribute("page", page); //현재페이지num
		model.addAttribute("startpage", startpage); //시작페이지num
		model.addAttribute("endpage", endpage); //끝페이지num
		model.addAttribute("maxpage", maxpage); //총 게시물개수
		model.addAttribute("listcount", listcount); //총 리스트
		model.addAttribute("qnalist", qnalist); //list화
		model.addAttribute("no", no);
		model.addAttribute("pp", pp);
		
		model.addAttribute("search", qna.getSearch());
		model.addAttribute("keyword", qna.getKeyword());
		
		System.out.println("list호출");
		return "qna/qnaList2";
	}
	
	//상세페이지
	@RequestMapping("/qna_detail/qna_no/{qna_no}/page/{page}/no/{no1}")
	public String qnaDetail(@PathVariable int qna_no, @PathVariable String page, @PathVariable int no1, Model model, Qna qna, HttpServletRequest request) {
		System.out.println("상세페이지호출controller");
		//조회수
		qs.hitupdate(qna_no);
		System.out.println("조회수 올라감");
		Qna qnalist = qs.select(qna_no);
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("page", page);
		model.addAttribute("no", no1);
		return "qna/qnaDetail";
	}
	
	
	//수정 session 체크
	//${path}/qna_updatecheck?qna_no=${qnalist.qna_no}&page=${page}'
	@RequestMapping("/qna_updatecheck")
	public String qnaUpdateCheck(Qna qna, String page, Model model) {
		System.out.println("수정 session체크");
		
		Qna qnalist = qs.select(qna.getQna_no());
		System.out.println("수정 session qnalist호출");
		
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("page", page);
		System.out.println("수정 session 공유");
		
		return "qna/qnaUpdateRecheck";
	}
	
	//수정폼
	@RequestMapping("/qna_updateform")
	public String qnaUpdateForm(Model model, Qna qna, String page) {
		System.out.println("수정페이지controller");
		Qna qnalist = qs.select(qna.getQna_no());
		// = Qna qnalist = qs.select(qna_no);
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("page", page);
		return "qna/qnaUpdate";
	}
	
	//수정완료
	@RequestMapping("/qna_update")
	public String qnaUpdate(Qna qna,String page, Model model) {
		System.out.println("수정완료버튼controller");
		int result = qs.update(qna);
		
		System.out.println("result:"+result);
		System.out.println("page:"+page);
		
		model.addAttribute("page", page);
		model.addAttribute("result", result);
		return "qna/qnaUpdateResult";
	}
	
	//삭제 재확인
	@RequestMapping("/qna_deletecheck/qna_no/{qna_no}/page/{page}")
	public String qnaDeleteCheck(Model model, Qna qna, @PathVariable int qna_no, @PathVariable String page) { 
		System.out.println("삭제재확인controller");
		Qna qnalist = qs.select(qna_no);
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("page", page);
		return "qna/qnaDeleteRecheck";
	}
	
	//게시물삭제
	@RequestMapping("/delete/qna_no/{qna_no}")
	public String Delete(Qna qna, @PathVariable int qna_no, HttpServletRequest request, Model model) {
		System.out.println("삭제controller");
		qs.delete(qna_no);
		return "redirect:/qnalist.do";
	}
	
	//답글form
	@RequestMapping("/qna_commentForm")
	public String commentFrom(Qna qna, Model model, String page) {
		System.out.println("commentform controller");
		
		Qna qnalist = qs.select(qna.getQna_no());
		System.out.println("commentform selectSQL문사용");
		
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("page", page);
		System.out.println("commentform에서 값공유");
		
		return "qna/qnaComment";
	}
	
	//답글등록-구현실패 
	@RequestMapping("qna_comment.do")
	public String insertComment(String page, Qna qna, Model model) {
		System.out.println("답글등록controller");
		qs.insertCom(qna);
		System.out.println("답글등록OK");
		
//		model.addAttribute("page", page);
		return "redirect:/qnalist.do?page="+page;
	}
	
	//통합검색
	@RequestMapping("total_search.do")
	public String totalSearcg() {
		
		
		
		return "qna/totalSearch";
	}
	
	
	
	
	
	
	
}











