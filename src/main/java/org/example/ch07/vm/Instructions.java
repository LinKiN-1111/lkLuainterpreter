package org.example.ch07.vm;

import org.example.ch07.api.ArithOp;
import org.example.ch07.api.CmpOp;
import org.example.ch07.api.LuaType;
import org.example.ch07.api.LuaVM;

import static java.lang.System.exit;
import static org.example.ch07.api.ArithOp.*;
import static org.example.ch07.api.CmpOp.*;
import static org.example.ch07.api.LuaType.*;

//该类实现了所有的指令操作,例如运算符指令,MOVE LOAD FOR等指令
public class Instructions {
    //由于Lua的寄存器好像也是从0开始的,导致这里寄存器的索引需要+1...其它的索引可以不管
    public static final int LFIELDS_PER_FLUSH = 50;

    // R(A) := R(B)   move指令虽然只用了两个寄存器,但是确实使用的是ABC模式
    public static void move(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1] + 1;
        int C = register[2];   // 不使用该值
        vm.copy(B, A);
    }

    public static void jmp(long ins, LuaVM vm) {
        int[] register = Instruction.AsBx(ins);
        int A = register[0];
        int sBx = register[1];
        vm.addPC(sBx);         //直接加就行了,应该是一个相对地址,所以这么操作
        if (A != 0){
            System.out.println("jmp error!");
            exit(0);
        }
    }

    //从索引A的位置,到A+B位置的栈中都设置为nil
    public static void loadNil(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1];
        int C = register[2];   // 不使用该值
        vm.pushNil();   //先存一个
        for(int i = 0;i<=B;i++){
            vm.copy(vm.getTop(),A+i);
        }
        vm.pop(1);  //然后再pop出来
        //这里假设已经使用了setTop方法保留了必要数量的栈空间,有了这个假设,我们可以先调用pushNil方法推入一个nil值，然后调用Copy方法,将nil
        //复制到指定寄存器中,然后将推入栈顶的nil值弹出...
    }

    //给单个寄存器设置布尔值,寄存器索引由操作数A
    public static void loadBool(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1];
        int C = register[2];
        //先设置布尔值
        if(B==1){
            vm.pushBoolean(true);
        }else{
            vm.pushBoolean(false);
        }
        vm.replace(A);
        //然后看C的值,根据C的值判断跳不跳过下一条指令
        if(C!=0){
            vm.addPC(1);
        }
    }

    public static void loadK(long ins, LuaVM vm) {
        int[] register = Instruction.ABx(ins);
        int A = register[0] + 1;
        int B = register[1];
        vm.getConst(B);    //直接推入栈顶
        vm.replace(A);     //然后直接放入目标寄存器
    }

    public static void loadKX(long ins, LuaVM vm) {
        int[] ints = Instruction.ABx(ins);
        int A = ints[0] + 1;
        int[] AX = Instruction.Ax(vm.fetch());
        vm.getConst(AX[0]);
        vm.replace(A);
    }

    //二元运算
    public static void binaryArith(long ins, LuaVM vm, ArithOp op) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1];
        int C = register[2];
        vm.getRK(B);   //推入栈顶
        vm.getRK(C);
        vm.arith(op);
        vm.replace(A);  //放到指定寄存器中
    }

    //一元运算
    public static void unaryArith(long ins, LuaVM vm, ArithOp op) {
        int[] register = Instruction.ABC(ins);
        //对操作数B所指定的寄存器里面的值进行运算,然后将结果放入到A中指定的寄存器中
        int A = register[0] + 1;
        int B = register[1] + 1;
        vm.pushValue(B);
        vm.arith(op);
        vm.replace(A);
    }

    /* arith */
    public static void add (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPADD ); } // +
    public static void sub (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPSUB ); } // -
    public static void mul (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPMUL ); } // *
    public static void mod (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPMOD ); } // %
    public static void pow (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPPOW ); } // ^
    public static void div (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPDIV ); } // /
    public static void idiv(int i, LuaVM vm) { binaryArith(i, vm, LUA_OPIDIV); } // //
    public static void band(int i, LuaVM vm) { binaryArith(i, vm, LUA_OPBAND); } // &
    public static void bor (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPBOR ); } // |
    public static void bxor(int i, LuaVM vm) { binaryArith(i, vm, LUA_OPBXOR); } // ~
    public static void shl (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPSHL ); } // <<
    public static void shr (int i, LuaVM vm) { binaryArith(i, vm, LUA_OPSHR ); } // >>
    public static void unm (int i, LuaVM vm) { unaryArith( i, vm, LUA_OPUNM ); } // -
    public static void bnot(int i, LuaVM vm) { unaryArith( i, vm, LUA_OPBNOT); } // ~

    public static void len(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1] + 1;
        vm.len(B);   //放到栈顶
        vm.replace(A);  //将栈顶的元素放到A寄存器中
    }


    public static void concat(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1] + 1;
        int C = register[2] + 1;
        int n = C-B + 1;
        for(int i=0; i< n;i++){
            vm.pushValue(B+i);
        }
        vm.concat(n);
        vm.replace(A);
    }

    public static void compare(long ins, LuaVM vm, CmpOp op) {
        int[] register = Instruction.ABC(ins);
        int A = register[0];
        int B = register[1];
        int C = register[2];
        vm.getRK(B);
        vm.getRK(C);
        if (vm.compare(-2, -1, op) != (A != 0)) {
            vm.addPC(1);
        }
        vm.pop(2);
    }

    public static void eq(int i, LuaVM vm) { compare(i, vm, LUA_OPEQ); } // ==
    public static void lt(int i, LuaVM vm) { compare(i, vm, LUA_OPLT); } // <
    public static void le(int i, LuaVM vm) { compare(i, vm, LUA_OPLE); } // <=

    public static void not(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1] + 1;
        vm.pushBoolean(!vm.toBoolean(B));
        vm.replace(A);
    }

    // if (R(B) <=> C) then R(A) := R(B) else pc++
    public static void testSet(int i, LuaVM vm) {
        int [] register = Instruction.ABC(i);
        int A = register[0] + 1;
        int B = register[1] + 1;
        int C = register[2];
        vm.pushValue(B);
        if (vm.toBoolean(B) == (C != 0)) {
            vm.copy(B, A);
        } else {
            vm.addPC(1);
        }
    }

    public static void test(int i, LuaVM vm) {
        int [] register = Instruction.ABC(i);
        int A = register[0] + 1;
        int C = register[2];
        if(vm.toBoolean(A) == (C != 0)) {
            vm.addPC(1);
        }
    }

    // R(A)-=R(A+2); pc+=sBx
    public static void forPrep(long ins, LuaVM vm) {
        int[] register = Instruction.AsBx(ins);
        int A = register[0] + 1;
        int sBX = register[1];

        if (vm.type(A) == LUA_TSTRING) {
            vm.pushNumber(vm.toNumber(A));
            vm.replace(A);
        }
        if (vm.type(A+1) == LUA_TSTRING) {
            vm.pushNumber(vm.toNumber(A + 1));
            vm.replace(A + 1);
        }
        if (vm.type(A+2) == LUA_TSTRING) {
            vm.pushNumber(vm.toNumber(A + 2));
            vm.replace(A + 2);
        }

        vm.pushValue(A);
        vm.pushValue(A+2);
        vm.arith(LUA_OPSUB);
        vm.replace(A);
        vm.addPC(sBX);
    }

    // R(A)+=R(A+2);
    // if R(A) <?= R(A+1) then {
    //   pc+=sBx; R(A+3)=R(A)
    // }
    public static void forLoop(long ins, LuaVM vm) {
        int[] register = Instruction.AsBx(ins);
        int A = register[0] + 1;
        int sBX = register[1];
        vm.pushValue(A);
        vm.pushValue(A+2);
        vm.arith(LUA_OPADD);
        vm.replace(A);
        boolean isPositiveStep = vm.toNumber(A+2) >= 0;   //判断是否大于0
        if (isPositiveStep && vm.compare(A, A+1, LUA_OPLE) ||
                !isPositiveStep && vm.compare(A+1, A, LUA_OPLE)) {
            // pc+=sBx; R(A+3)=R(A)
            vm.addPC(sBX);
            vm.copy(A, A+3);
        }
    }


    /* table ins */
    // R(A) := {} (size = B,C)
    public static void newTable(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;  //寄存器
        int B = register[1];  //narry
        int C = register[2];  //nRec
        vm.createTable(FPB.fb2int(B), FPB.fb2int(C));   //创建一个Table对象放入栈顶
        vm.replace(A);          //将栈顶的table对象放置到A寄存器
    }

    // R(A) := R(B)[RK(C)]
    public static void getTable(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;   //target
        int B = register[1] + 1;   //table
        int C = register[2] ;   //index
        vm.getRK(C);            //推进栈顶
        LuaType table = vm.getTable(B); //从table中获取,然后放入到栈顶
        vm.replace(A);
    }

    // R(A)[RK(B)] := RK(C)
    public static void setTable(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A = register[0] + 1;
        int B = register[1];
        int C = register[2];
        vm.getRK(B);        //key推进栈顶     后出key
        vm.getRK(C);        //value推进栈顶   因为先出value
        vm.setTable(A);
    }

    // R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B
    public static void setList(long ins, LuaVM vm) {
        int[] register = Instruction.ABC(ins);
        int A =  register[0] + 1;
        int B = register[1];
        int C = register[2];
        if(C > 0){
            C = C - 1;
        }else{
            C = Instruction.ABx(vm.fetch())[1];  //EXTRAARG指令中指定
        }
        int idx = C * LFIELDS_PER_FLUSH;
        for (int j = 1; j <= B; j++) {
            idx++;
            vm.pushValue(A + j);
            vm.setI(A, idx);
        }
    }


}
