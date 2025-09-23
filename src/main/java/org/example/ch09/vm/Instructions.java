package org.example.ch09.vm;

import org.example.ch09.api.ArithOp;
import org.example.ch09.api.CmpOp;
import org.example.ch09.api.LuaVM;

import static org.example.ch08.api.ArithOp.*;
import static org.example.ch08.api.CmpOp.*;
import static org.example.ch08.api.LuaType.LUA_TSTRING;



//该类实现了所有的指令操作,例如运算符指令,MOVE LOAD FOR等指令
public class Instructions {
    //由于Lua的寄存器好像也是从0开始的,导致这里寄存器的索引需要+1...其它的索引可以不管
    public static final int LFIELDS_PER_FLUSH = 50;

    // R(A) := R(B)   move指令虽然只用了两个寄存器,但是确实使用的是ABC模式
    public static void move(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        vm.copy(B, A);
    }

    // pc+=sBx; if (A) close all upvalues >= R(A - 1)
    public static void jmp(long ins, LuaVM vm) {
        int[] registers = Instruction.AsBx(ins);
        int A = registers[0];
        int sBx = registers[1];
        vm.addPC(sBx);         //直接加就行了,应该是一个相对地址,所以这么操作
        if (A != 0){
            throw new RuntimeException("todo: jmp!");
        }
    }

    //从索引A的位置,到A+B位置的栈中都设置为nil
    // R(A), R(A+1), ..., R(A+B) := nil
    public static void loadNil(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];

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
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        int C = registers[2];
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
        int[] registers = Instruction.ABx(ins);
        int A = registers[0] + 1;
        int B = registers[1];
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
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        int C = registers[2];
        vm.getRK(B);   //推入栈顶
        vm.getRK(C);
        vm.arith(op);
        vm.replace(A);  //放到指定寄存器中
    }

    //一元运算
    public static void unaryArith(long ins, LuaVM vm, ArithOp op) {
        int[] registers = Instruction.ABC(ins);
        //对操作数B所指定的寄存器里面的值进行运算,然后将结果放入到A中指定的寄存器中
        int A = registers[0] + 1;
        int B = registers[1] + 1;
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
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        vm.len(B);   //放到栈顶
        vm.replace(A);  //将栈顶的元素放到A寄存器中
    }


    public static void concat(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        int C = registers[2] + 1;
        int n = C-B + 1;
        for(int i=0; i< n;i++){
            vm.pushValue(B+i);
        }
        vm.concat(n);
        vm.replace(A);
    }

    private static void compare(long ins, LuaVM vm, CmpOp op) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0];
        int B = registers[1];
        int C = registers[2];
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
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        vm.pushBoolean(!vm.toBoolean(B));
        vm.replace(A);
    }

    // if (R(B) <=> C) then R(A) := R(B) else pc++
    public static void testSet(int i, LuaVM vm) {
        int [] registers = Instruction.ABC(i);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        int C = registers[2];
        vm.pushValue(B);
        if (vm.toBoolean(B) == (C != 0)) {
            vm.copy(B, A);
        } else {
            vm.addPC(1);
        }
    }

    public static void test(int i, LuaVM vm) {
        int [] registers = Instruction.ABC(i);
        int A = registers[0] + 1;
        int C = registers[2];
        if(vm.toBoolean(A) != (C != 0)) {
            vm.addPC(1);
        }
    }

    // R(A)-=R(A+2); pc+=sBx
    public static void forPrep(long ins, LuaVM vm) {
        int[] registers = Instruction.AsBx(ins);
        int A = registers[0] + 1;
        int sBX = registers[1];

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
        int[] registers = Instruction.AsBx(ins);
        int A = registers[0] + 1;
        int sBX = registers[1];
        vm.pushValue(A+2);
        vm.pushValue(A);
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
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;  //寄存器
        int B = registers[1];  //narry
        int C = registers[2];  //nRec
        vm.createTable(FPB.fb2int(B), FPB.fb2int(C));   //创建一个Table对象放入栈顶
        vm.replace(A);          //将栈顶的table对象放置到A寄存器
    }

    // R(A) := R(B)[RK(C)]
    public static void getTable(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;   //target
        int B = registers[1] + 1;   //table
        int C = registers[2];   //index
        vm.getRK(C);            //推进栈顶
        vm.getTable(B); //从table中获取,然后放入到栈顶
        vm.replace(A);
    }

    // R(A)[RK(B)] := RK(C)
    public static void setTable(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        int C = registers[2];
        vm.getRK(B);        //key推进栈顶     后出key
        vm.getRK(C);        //value推进栈顶   因为先出value
        vm.setTable(A);
    }

    // R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B
    public static void setList(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A =  registers[0] + 1;
        int B = registers[1];
        int C = registers[2];

        if(C > 0){
            C = C - 1;
        }else{
            C = Instruction.Ax(vm.fetch())[0];  //EXTRAARG指令中指定
        }

        boolean bIsZero = B == 0;
        if (bIsZero) {
            B = ((int) vm.toInteger(-1)) - A - 1;
            vm.pop(1);
        }

        int idx = C * LFIELDS_PER_FLUSH;

        for (int j = 1; j <= B; j++) {
            idx++;
            vm.pushValue(A + j);
            vm.setI(A, idx);
        }


        if(bIsZero){
            for(int i = vm.registerCount()+1; i <= vm.getTop(); i++){
                idx++;
                vm.pushValue(i);
                vm.setI(A,idx);
            }

            vm.setTop(vm.registerCount());
        }
    }

    // R(A) := closure(KPROTO[Bx])
    // 把当前L码函数的子函数原型实例化为闭包,放入操作数A指定的寄存器中
    public static void closure(long ins, LuaVM vm) {
        int[] registers = Instruction.ABx(ins);
        int A = registers[0] + 1;
        int Bx = registers[1];
        vm.loadProto(Bx);   //获取当前函数的子函数原型,并将其推入栈顶
        vm.replace(A);
    }

    // R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1))
    public static void call(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        int C = registers[2];

        int nArgs = pushFuncAndArgs(A,B ,vm);  //该被调用函数和参数值推入栈顶
        vm.call(nArgs,C-1); //返回值数量由C获取?
        popResults(A,C,vm);   //把返回值移动到适当的寄存器
    }


    //我们需要将返回值推入栈顶...
    //return R(A), ... ,R(A+B-2)
    public static void _return(long  ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        if(B == 1){
            //do nothing
        }else if(B>1){
            for(int i = A; i <= A + B-2; i++){
                vm.pushValue(i);
            }
        }else {
            fixStack(A,vm);
        }
    }

    //传递当前函数的变长参数到连续多个寄存器中
    // R(A), R(A+1), ..., R(A+B-2) = vararg
    public static void vararg(long  ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];

        if(B != 1){
            vm.loadVararg(B-1);
            popResults(A,B,vm);
        }
    }

    //这里只是用最简单的方式实现tailCall
    public static void tailCall(long ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1];
        int C = 0;

        int nArgs = pushFuncAndArgs(A,B ,vm);
        vm.call(nArgs,C-1);
        popResults(A,C,vm);
    }

    public static void self(long  ins, LuaVM vm) {
        int[] registers = Instruction.ABC(ins);
        int A = registers[0] + 1;
        int B = registers[1] + 1;
        int C = registers[2];

        vm.copy(B,A+1);
        vm.getRK(C);
        vm.getTable(B);
        vm.replace(A);

    }

    private static int pushFuncAndArgs(int A, int B, LuaVM vm) {
        if(B>=1){
            //若B大于0就简单了,需要传递的参数是B-1个,有一个是原型吧?
            for(int i = A; i < A + B; i++){
                vm.pushValue(i);
            }
            return B-1;
        }else{
            //这种情况说明有参数是之前函数的返回值,之前函数的返回值,
            fixStack(A,vm);
            return vm.getTop() - vm.registerCount() -1;   //registerCount获取当前函数的寄存器个数,后续定义
        }
    }

    //如果操作数C大于1,则返回值数量是C-1,循环调用Repalce方法把栈顶返回值移动到相应寄存器即可
    //如果操作数C=1,则说明返回值数量是0,不需要任何处理
    //如果操作数C=0,则需要把被调用的返回值全部返回,对于这种情况,干脆把这个返回值线留在栈顶..
    private static void popResults(int A, int C, LuaVM vm) {
        if(C==1){
            //no result
        }else if(C>1){
            //直接设置返回值到目标寄存器
            for(int i = A+C-2; i >= A; i--){
                vm.replace(i);
            }
        }else{
            //将返回值留在栈顶,存放返回值的目标寄存器到栈顶
            vm.pushInteger(A);
        }
    }

    //修复堆栈顺序的方法?
    private static void fixStack(int A, LuaVM vm) {
        //这里的A是目标函数以及目标参数寄存器起始地址
        int X = (int) vm.toInteger(-1);   //获取
        vm.pop(1);
        for(int j = A; j < X; j++){
            vm.pushValue(j);
        }
        vm.rotate(vm.registerCount()+1,X-A);
    }


}
