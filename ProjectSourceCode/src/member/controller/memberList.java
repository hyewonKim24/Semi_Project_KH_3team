package member.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.model.*;
import service.member.memberService;

/**
 * Servlet implementation class wreqList
 */
@WebServlet("/memberList.do")
public class memberList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public memberList() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	protected void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 로그인 x
		memberVO mvo = (memberVO) request.getSession().getAttribute("member");
		if (mvo == null || mvo.equals("") || mvo.equals("null")) {
			PrintWriter writer = response.getWriter();
			writer.println("<script>alert('로그인을 해주세요.'); location.href='./member/memberLogin.jsp';</script>");
			writer.flush();
			writer.close();
		} else {
			memberService msv = new memberService();
			// 페이징
			int pageSize = 10; // 페이지 당 글 수
			int pageBlock = 10; // 페이지 링크 수
			try {
				// 총 글 개수
				int nCount = msv.getBoardCount();
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
				prev = startPage - 1;
				next = endPage + 1;
//			if (startPage != 1) {
//			}
//			if (endPage > pageCount) {
//			}
				System.out.println(prev + "이전 - 다음" + next);
				List<memberVO> mlist = msv.getBoardPage(startRnum, endRnum); /* 변경 : 메소드 */

				// 보내주기

				request.setAttribute("startPage", startPage);
				request.setAttribute("endPage", endPage);
				request.setAttribute("PageNum", currentPage);
				request.setAttribute("pageCount", pageCount);
				request.setAttribute("prev", prev);
				request.setAttribute("next", next);

				request.setAttribute("mlist", mlist); /* 변경 : el태그 - jsp이랑 맞추기 */
				System.out.println(mlist.size() + ", " + startPage + ", " + endPage);
				RequestDispatcher disp1 = request.getRequestDispatcher("./manage/mManage1.jsp"); /* 변경 : 경로 */
				disp1.forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();

			}
		}

	}
}
