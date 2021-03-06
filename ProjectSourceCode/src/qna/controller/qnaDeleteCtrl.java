package qna.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.notice.NoticeService;
import service.qna.QnAService;


/**
 * Servlet implementation class qnaDelete
 */
@WebServlet("/qnaDelete.do")
public class qnaDeleteCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public qnaDeleteCtrl() {
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

	private void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("삭제 서블릿 진입");
		PrintWriter out = response.getWriter();
		QnAService qsv = new QnAService();
		try {
			int qref = Integer.parseInt(request.getParameter("qref"));
			System.out.println(qref);
			System.out.println(request.getParameter("qref"));

			int result = qsv.qnaDelete(qref);
			if (result == 1) {
					System.out.println("삭제 성공");
					out.append("<script>alert('삭제 성공 했습니다.');</script>");
					RequestDispatcher disp = request.getRequestDispatcher("qnaList.do");
					disp.forward(request, response);
			} else {
				RequestDispatcher disp = request.getRequestDispatcher("qnaList.do");
				disp.forward(request, response);
				System.out.println("삭제 실패 또는 원글답글 모두 삭제");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("삭제 중 오류 발생");
		}
		out.flush();
		out.close();
	}
}
