package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//TOP画面から編集画面に遷移
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());
		//入力内容の取得
		String requestId = request.getParameter("messageId");
		List<String> errorMessages = new ArrayList<String>();

		//メッセージIDが数字かつ空でないか確認
		if (requestId == null || !(requestId.matches("^([1-9][0-9]*)$"))) {
			errorMessages.add("不正なパラメータが入力されました");
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("./").forward(request, response);
			return;
		}

		//メッセージIDからメッセージを抽出
		int messageId = Integer.parseInt(requestId);
		Message message = new MessageService().select(messageId);

		//メッセージIDが存在しているか確認
		if (message == null) {
			errorMessages.add("不正なパラメータが入力されました");
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("./").forward(request, response);
			return;
		}

		//メッセージをセットし編集画面に遷移
		request.setAttribute("message", message);
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}

	//更新ボタンを押してメッセージを更新
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		Message message = getMessage(request);

		//エラーメッセージ表示。テキストを保持。
		if (!isValid(message.getText(), errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("message", message);
			request.getRequestDispatcher("/edit.jsp").forward(request, response);
			return;
		}
		//メッセージ更新処理
		new MessageService().update(message);
		//TOP画面に遷移
		response.sendRedirect("./");
	}

	//beansに更新したい入力内容を入れる
	private Message getMessage(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Message message = new Message();
		message.setId(Integer.parseInt(request.getParameter("messageId")));
		message.setText(request.getParameter("text"));
		return message;
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}