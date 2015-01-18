import java.io.*;
import java.util.*;

public class Whitespace {
	private static final int SPACE = 0;
	private static final int TAB   = 1;
	private static final int LF    = -1;
	private static final int END   = -2;
	
	static Queue command; // ���ߗ���L��
	static Stack stack;   // ���s���Ɏg���X�^�b�N
	
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
	// �����Ȃ���s����͖̂ʓ|�Ȃ̂ŁA�܂��̓t�@�C���̒��g�����X�g�ɏ����o��
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
				// ��͂�readLine()�ł͉��s�܂ł͓ǂ�ł���Ȃ��炵���B
				command.push(LF);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// �{��
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
//				if (cmd == TAB)   heapAccess(); // ������
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
			// �X���C�h�̈Ӗ����킩���̂�TAB-LF�͂Ȃ�
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
			if (cmd == SPACE) { // �X�^�b�N���當�����P�����o��
				int n = stack.pop();
				System.out.print((char)n);
			} else if (cmd == TAB) { // �X�^�b�N���琔�����P�����o��
				int n = stack.pop();
				System.out.print(n);
			}
		} else if (cmd == TAB) {
			cmd = command.pop();
			if (cmd == SPACE) { // �P������ǂ�ŃX�^�b�N�ɐς�
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				try {
					System.out.print("character -> ");
					char c = (bufferedReader.readLine()).charAt(0);
					stack.push((int)c);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (cmd == TAB) { // �P������ǂ�ŃX�^�b�N�ɐς�
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
		if (stack.size() < 2) return; // �v�Z�Ɏ��s
		int op2 = stack.pop();
		int op1 = stack.pop(); // �ŏ��ɐς܂ꂽ������
		
		int cmd = command.pop();
		if (cmd == SPACE) {
			cmd = command.pop();
			if (cmd == SPACE) { // ���Z
				stack.push(op1 + op2);
			} else if (cmd == TAB) { // ���Z
				stack.push(op1 - op2);
			}
		} else if (cmd == TAB) {
			cmd = command.pop();
			if (cmd == SPACE) { // ��Z
				stack.push(op1 * op2);
			} else if (cmd == TAB) { // ���Z
				stack.push(op1 / op2);
			}
		}
		// �c�O�Ȃ��畂���������Z�͌v�Z�ł��Ȃ��B�ꕔ�d�l���قȂ�i��Z�R�}���h�j
	}
	
	// ���O�̃X�^�b�N�ƃL���[�Bpush�̓���ȊO�͓���
	private static class Queue {
		ArrayList<Integer> array;
		
		Queue() { array = new ArrayList<Integer>(); }
		
		public void push(int n) {
			array.add(new Integer(n));
		}
		public int pop() {
			// ����ȏ�Ԃ����̂��Ȃ��Ƃ��AQueue��END��Ԃ�
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
			// ����ȏ�Ԃ����̂��Ȃ��Ƃ��AQueue��END��Ԃ�
			if (array.size() == 0) return END;
			int n = array.get(0).intValue();
			array.remove(0);
			return n;
		}
		public int size() { return array.size(); }
		public void copy(int n) { push(array.get(n)); }
		public void swap() {
			if (size() < 2) return; // swap�Ɏ��s
			int op1 = pop(), op2 = pop();
			push(op1); push(op2);
		}
	}
}
