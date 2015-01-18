import java.io.*;
import java.util.*;

public class Whitespace {
	private static final int SPACE = 0;
	private static final int TAB   = 1;
	private static final int LF    = -1;
	private static final int END   = -2;
	
	static Queue command; // 命令列を記憶
	static Stack stack;   // 実行時に使うスタック
	
	Whitespace() {
		init();
	}
	private static void init() {
		command = new Queue();
		stack   = new Stack();
	}
	
	public static void main (String args[]) {
		init();
		
		if (args.length < 1) {
			// error
			System.exit(0);
		} else {
			preprocess(args[0]);
			run();
		}
	}
	// いきなり実行するのは面倒なので、まずはファイルの中身をリストに書き出す
	private static void preprocess(String filename) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "MS932"));
			String s;
			while ((s = bufferedReader.readLine()) != null) {
				for (int i = 0; i < s.length(); i++) {
					switch (s.charAt(i)) {
					case ' ':
						command.push(SPACE);
						break;
					case '\t':
						command.push(TAB);
						break;
					case '\n':
						command.push(LF);
						break;
					default:
						// comment
						break;
					}
				}
				// やはりreadLine()では改行までは読んでくれないらしい。
				command.push(LF);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 本体
	private static void run() {
		int cmd;
		while ((cmd = command.pop()) != END) {
			switch (cmd) {
			case SPACE:
				stackOperation();
				break;
			case TAB:
				cmd = command.pop();
				if (cmd == SPACE) calculate();
//				if (cmd == TAB)   heapAccess(); // 未実装
				if (cmd == LF)    io();
				break;
			case LF:
				return;
			}
		}
	}
	private static void stackOperation() {
		switch (command.pop()) {
		case SPACE:
			stack.push(getValue());
			break;
		case TAB:
			int cmd = command.pop();
			if (cmd == SPACE) stack.copy(getValue());
			// スライドの意味がわからんのでTAB-LFはなし
			break;
		case LF:
			cmd = command.pop();
			if (cmd == SPACE) stack.copy(0);
			else if (cmd == TAB) stack.swap();
			else if (cmd == LF) stack.pop();
			break;
		}
	}
	private static int getValue() {
		int cmd;
		int n = 0;
		while ((cmd = command.pop()) != END) {
			switch (cmd) {
			case SPACE:
				n = n << 1;
				break;
			case TAB:
				n = (n << 1) + 1;
				break;
			case LF:
				return n;
			}
		}
		return n;
	}
	private static void io() {
		int cmd = command.pop();
		if (cmd == SPACE) {
			cmd = command.pop();
			if (cmd == SPACE) { // スタックから文字を１つ書き出す
				int n = stack.pop();
				System.out.print((char)n);
			} else if (cmd == TAB) { // スタックから数字を１つ書き出す
				int n = stack.pop();
				System.out.print(n);
			}
		} else if (cmd == TAB) {
			cmd = command.pop();
			if (cmd == SPACE) { // １つ文字を読んでスタックに積む
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				try {
					System.out.print("character -> ");
					char c = (bufferedReader.readLine()).charAt(0);
					stack.push((int)c);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (cmd == TAB) { // １つ数字を読んでスタックに積む
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				try {
					System.out.print("number -> ");
					int n = Integer.valueOf(bufferedReader.readLine()).intValue();
					stack.push(n);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static void calculate() {
		if (stack.size() < 2) return; // 計算に失敗
		int op2 = stack.pop();
		int op1 = stack.pop(); // 最初に積まれた側が左
		
		int cmd = command.pop();
		if (cmd == SPACE) {
			cmd = command.pop();
			if (cmd == SPACE) { // 加算
				stack.push(op1 + op2);
			} else if (cmd == TAB) { // 減算
				stack.push(op1 - op2);
			}
		} else if (cmd == TAB) {
			cmd = command.pop();
			if (cmd == SPACE) { // 乗算
				stack.push(op1 * op2);
			} else if (cmd == TAB) { // 除算
				stack.push(op1 / op2);
			}
		}
		// 残念ながら浮動小数除算は計算できない。一部仕様が異なる（乗算コマンド）
	}
	
	// 自前のスタックとキュー。pushの動作以外は同じ
	private static class Queue {
		ArrayList<Integer> array;
		
		Queue() { array = new ArrayList<Integer>(); }
		
		public void push(int n) {
			array.add(new Integer(n));
		}
		public int pop() {
			// これ以上返すものがないとき、QueueはENDを返す
			if (array.size() == 0) return END;
			int n = array.get(0).intValue();
			array.remove(0);
			return n;
		}
//		public int size() { return array.size(); }
//		public void copy(int n) { push(array.get(n); }
	}
	private static class Stack {
		ArrayList<Integer> array;
		
		Stack() { array = new ArrayList<Integer>(); }
		
		public void push(int n) {
			array.add(0, new Integer(n));
		}
		public int pop() {
			// これ以上返すものがないとき、QueueはENDを返す
			if (array.size() == 0) return END;
			int n = array.get(0).intValue();
			array.remove(0);
			return n;
		}
		public int size() { return array.size(); }
		public void copy(int n) { push(array.get(n)); }
		public void swap() {
			if (size() < 2) return; // swapに失敗
			int op1 = pop(), op2 = pop();
			push(op1); push(op2);
		}
	}
}
