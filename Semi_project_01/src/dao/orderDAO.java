package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;

import cart.model.CartListVO;
import member.model.*;
import order.model.*;

public class orderDAO {
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Statement stmt = null;

	private void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 공지사항 목록 페이징 - 공지사항 총 글 개수
	public int getBoardCount(Connection conn) throws SQLException {
		int cnt = 0;
		String sql = "select COUNT(*) from order2";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				cnt = rs.getInt(1);
			}
		} finally {
			close();
		}
		return cnt;
	}

	// 공지사항 전체 목록 메소드
	public List<orderVO> getBoardPage(Connection conn, int start, int end) throws SQLException {
		List<orderVO> olist = new ArrayList<orderVO>();
		orderVO ovo = null;
		String sql = "select * from (select ROWNUM rnum, o.* from (select * from order2 order by ono desc) o) where rnum >= ? and rnum <= ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				do {
					ovo = new orderVO();
					ovo.setOno(rs.getInt("ono"));
					ovo.setMid(rs.getString("mid"));
					ovo.setOtotalamount(rs.getInt("oamount"));
					ovo.setOtotalprice(rs.getInt("ototalprice"));
					ovo.setOname(rs.getString("oname"));
					ovo.setOphone(rs.getString("ophone"));
					ovo.setOpay(rs.getString("opay"));
					olist.add(ovo);
				} while (rs.next());
			}
		} finally {
			close();
		}
		return olist;
	}

	public List<orderVO> orderList(Connection conn) throws SQLException {
		List<orderVO> oList = null;
		String sql = "select * from order2";
		try {
			System.out.println("db에 접근했습니다");
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				oList = new ArrayList<orderVO>();
				do {
					orderVO ovo = new orderVO();
					ovo.setOno(rs.getInt("ono"));
					ovo.setMid(rs.getString("mid"));
					ovo.setOtotalamount(rs.getInt("oamount"));
					ovo.setOtotalprice(rs.getInt("ototalprice"));
					ovo.setOname(rs.getString("oname"));
					ovo.setOphone(rs.getString("ophone"));
					ovo.setOpay(rs.getString("opay"));
					oList.add(ovo);
				} while (rs.next());
			}
		} finally {
			close();
		}
		return oList;
	}
	public List<orderVO> orderDetail(Connection conn, String mid) throws SQLException {
		List<orderVO> odList = null;
		
		String sql = "select o.ono, o.odate, o.oamout, n.dno, b.bisbn, b.bcover, b.btitle, b.bpriceStandard, bpricesales, o.opay, o.odprice, o.ototalprice, o.oname, o.ophone, o.oaddr1" 
				+ "from order2 o, neworder2 n, bookcover bc, (select bisbn, btitle, bcover, bpriceStandard, bpriceSales from book) b"
				+ "where o.mid = ? and n.bisbn=b.bisbn and n.dno = bc.dno";
		try {
			System.out.println("orderdetail까지 옴");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				odList = new ArrayList<orderVO>();
				
				do {
				orderVO ovo = new orderVO();
				ovo.setMid(rs.getString("mid"));
				ovo.setOno(rs.getInt("ono"));
				ovo.setOdate(rs.getDate("odate"));
				ovo.setOamount(rs.getInt("oamount"));
				ovo.setDno(rs.getInt("dno"));
				ovo.setBISBN(rs.getString("BISBN"));
				ovo.setBcover(rs.getString("bcover"));
				ovo.setBtitle(rs.getString("btitle"));
				ovo.setBpriceStandard(rs.getInt("bpriceStandard"));
				ovo.setBpricesales(rs.getInt("bpricesales"));
				ovo.setOpay(rs.getString("opay"));
				ovo.setOdprice(rs.getInt("odprice"));
				ovo.setOtotalprice(rs.getInt("ototalprice"));
				ovo.setOname(rs.getString("oname"));
				ovo.setOphone(rs.getString("ophone"));
				ovo.setOaddr1("oaddr1");
				odList.add(ovo);
				}while(rs.next());
			}
		} finally {
			close();
		}
		return odList;
	}
	
	//은실 코드
	
	

	// 장바구니에서 불러오기
	public List<CartListVO> orderList(Connection conn, int[] chks) throws SQLException {
		List<CartListVO> cartlist = new ArrayList<CartListVO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String params = "";
		for (int i = 0; i < chks.length; i++) {
			params += chks[i];
			if (i < chks.length - 1)
				params += ",";
		}

		String sql = "select b.bcover, b.btitle, c.oamount, b.bpriceStandard, b.bpriceSales, dno2, c.bisbn, c.mid, bc.ddesc, bc.dimg, bc.dprice, c.cno"
				+ " from (select cno,mid,nvl(dno,0) as dno2, bisbn,oamount from cart) c, book b, bookcover bc"
				+ " where cno IN (" + params + ") and c.bisbn=b.bisbn and dno2=bc.dno";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				do {
					CartListVO vo = new CartListVO();
					vo.setBcover(rs.getString(1));
					vo.setBtitle(rs.getString(2));
					vo.setOamount(rs.getInt(3));
					vo.setBpricestandard(rs.getInt(4));
					vo.setBpricesales(rs.getInt(5));
					vo.setDno(rs.getInt(6));
					vo.setBisbn(rs.getString(7));
					vo.setDdesc(rs.getString(9));
					vo.setDimg(rs.getString(10));
					vo.setDprice(rs.getInt(11));
					vo.setCno(rs.getInt(12));

					cartlist.add(vo);
				} while (rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return cartlist;
	};

	// 바로 주문하기
	public List<CartListVO> directOrderList(Connection conn, String bisbn, int dno) throws SQLException {
		List<CartListVO> cartlist = new ArrayList<CartListVO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select b.bcover, b.btitle, b.bpriceStandard, b.bpricesales, bc.ddesc, bc.dimg, bc.dprice"
				+ " from book b, bookcover bc" + " where b.BISBN=? and bc.dno=?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bisbn);
			pstmt.setInt(2, dno);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				CartListVO vo = new CartListVO();
				vo.setBcover(rs.getString(1));
				vo.setBtitle(rs.getString(2));
				vo.setBpricestandard(rs.getInt(3));
				vo.setBpricesales(rs.getInt(4));
				vo.setBisbn(bisbn);
				vo.setDdesc(rs.getString(5));
				vo.setDimg(rs.getString(6));
				vo.setDprice(rs.getInt(7));
				vo.setDno(dno);
				
				cartlist.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return cartlist;
	};
	

	// 주문완료(인서트 order)
	public int orderInsert(Connection conn, orderVO vo) {
		System.out.println("Insert 시작");
		int result = 0;
		String sql = "insert into order2(ono, mid, odate, odprice, ototalprice, osalesprice, oname, ophone, oaddr1, oaddr2, oaddr3, omemo, opay, ototalamount, ostatus) values(ono_se.nextval,?,sysdate,2500,?,?,?,?,?,?,?,?,?,?,'주문완료')";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getMid());
			pstmt.setInt(2, vo.getOtotalprice());
			pstmt.setInt(3, vo.getOsalesprice());
			pstmt.setString(4, vo.getOname());
			pstmt.setString(5, vo.getOphone());
			pstmt.setString(6, vo.getOaddr1());
			pstmt.setString(7, vo.getOaddr2());
			pstmt.setString(8, vo.getOaddr3());
			pstmt.setString(9, vo.getOmemo());
			pstmt.setString(10, vo.getOpay());
			pstmt.setInt(11, vo.getOtotalamount());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 주문완료(인서트 neworder)
	public int orderInsert2(Connection conn, int[] dno, String[] bisbn, int[] oamount) {
		System.out.println("Insert 시작");
		for (int i = 0; i < bisbn.length; i++) {
			System.out.println("dno:" + dno[i]);
			System.out.println("bisbn" + bisbn[i]);
			System.out.println("oamount:" + oamount[i]);
		}
		int result = 0;
		String sql = "insert into neworder2(ono, dno, bisbn, oamount) values(ono_se.currval,?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < bisbn.length; i++) {
//				pstmt.setInt(1, vo2.get(i).getDno());
//				pstmt.setString(2, vo2.get(i).getBisbn());
//				pstmt.setInt(3, vo2.get(i).getOamount());
				pstmt.setInt(1, dno[i]);
				pstmt.setString(2, bisbn[i]);
				pstmt.setInt(3, oamount[i]);
				System.out.println(dno[i] + "으" + bisbn[i] + ":" + oamount[i]);
				result += pstmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
//		}
//		if(vo2.size()!=result) {
//			return -1;
//		}

		return result;
	}
}
