package qna.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import notice.model.NoticeVO;
import qna.model.QnAVO;
import service.notice.NoticeService;
import service.qna.QnAService;

/**
 * Servlet implementation class qnaSearchCtrl
 */
@WebServlet("/qnaSearch.do")
public class qnaSearchCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public qnaSearchCtrl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		execute(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		execute(request, response);
	}

	protected void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		QnAService qsv = new QnAService();
		String qtype = request.getParameter("qtype");
		System.out.println("qtype:" + qtype);
		String qSearch = request.getParameter("qSearch");
		System.out.println("qsearch" + qSearch);
		// 페이징
		int pageSize = 10; // 페이지 당 글 수
		int pageBlock = 10; // 페이지 링크 수
		try {
			// 총 글 개수
			int nCount = qsv.qnaSearchCount(qtype, qSearch); /* 변경 : 메소드 */
			System.out.println(nCount);

			// 페이지 수 초기화
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			} else if (pageNum.equals("")) {
				pageNum = "1";
			}
			// startPage , endPage 구하는 식
			int currentPage = 1;
			try {
				currentPage = Integer.parseInt(pageNum);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int pageCount = (nCount / pageSize) + (nCount % pageSize == 0 ? 0 : 1);
			int startPage = 1;
			int endPage = 1;
			if (currentPage % pageBlock == 0) {
				startPage = ((currentPage / pageBlock) - 1) * pageBlock + 1;
			} else {
				startPage = ((currentPage / pageBlock)) * pageBlock + 1;
			}
			endPage = startPage + pageBlock - 1;
			if (endPage > pageCount)
				endPage = pageCount;

			// 페이징 rownum 구하기
			int startRnum = ((currentPage - 1) * pageSize) + 1; // 거의 공식
			int endRnum = startRnum + pageSize - 1; // currentPage*pageSize
			System.out.println(startRnum + " - " + endRnum);

			// 이전 다음 기능
			int prev = 1;
			int next = 1;
			next = endPage + 1;
			prev = startPage - 1;
			if (startPage != 1) {
				prev = startPage - 1;
			}
			if (endPage > pageCount) {
				next = endPage + 1;
			}
			System.out.println(prev + "이전 - 다음" + next);

			//보내주기
			if (qSearch == null || qSearch == "") {
				request.setAttribute("search_error", "검색어를 입력해주세요");
				System.out.println("검색어가 null 인 경우");
				RequestDispatcher disp = request.getRequestDispatcher("qnaList.do");
				disp.forward(request, response);
			} else {
				try {
					List<QnAVO> searchList = qsv.qnaSearch(qtype, qSearch, startRnum, endRnum);
					// 보내주기
					if (searchList != null) {
						request.setAttribute("startPage", startPage);
						request.setAttribute("endPage", endPage);
						request.setAttribute("PageNum", currentPage);
						request.setAttribute("pageCount", pageCount);
						request.setAttribute("prev", prev);
						request.setAttribute("next", next);
						request.setAttribute("qtype", qtype);
						if (qtype == null || qtype.equals("")) {
							qtype = "전체";
						}
						request.setAttribute("qsearch", qSearch);
						request.setAttribute("searchList", searchList);
						System.out.println("검색 수 " + searchList.size() + ", " + startPage + ", " + endPage);
						RequestDispatcher disp = request.getRequestDispatcher("./qna/qnaSearch.jsp");
						disp.forward(request, response);
					} else {
						// 에러 페이지
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}