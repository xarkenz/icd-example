source_filename = "test_program.txt"
target triple = "x86_64-pc-linux-gnu"

@print_int_fstring = private unnamed_addr constant [4 x i8] c"%d\0A\00"

define i32 @square(i32 %0) {
.block.0:
	%x = alloca i32
	store i32 %0, i32* %x
	%1 = load i32, i32* %x
	%2 = load i32, i32* %x
	%3 = mul nsw i32 %1, %2
	ret i32 %3
}

define i32 @gcd(i32 %0, i32 %1) {
.block.0:
	%a = alloca i32
	store i32 %0, i32* %a
	%b = alloca i32
	store i32 %1, i32* %b
	br label %.block.1
.block.1:
	%2 = load i32, i32* %b
	%3 = icmp sgt i32 %2, 1
	br i1 %3, label %.block.2, label %.block.3
.block.2:
	%temp = alloca i32
	%4 = load i32, i32* %a
	%5 = load i32, i32* %b
	%6 = srem i32 %4, %5
	store i32 %6, i32* %temp
	%7 = load i32, i32* %b
	store i32 %7, i32* %a
	%8 = load i32, i32* %temp
	store i32 %8, i32* %b
	br label %.block.1
.block.3:
	%9 = load i32, i32* %a
	ret i32 %9
}

define i32 @main() {
.block.0:
	%0 = add nsw i32 10, 8
	%1 = sdiv i32 6, 6
	%2 = mul nsw i32 %1, 2
	%3 = sub nsw i32 %0, %2
	%4 = sdiv i32 8, 4
	%5 = add nsw i32 %3, %4
	%6 = sub nsw i32 %5, 1
	%7 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %6)
	%8 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 5)
	%9 = sub nsw i32 0, 5
	%10 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %9)
	%11 = sub nsw i32 5, 3
	%12 = add nsw i32 %11, 2
	%13 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %12)
	%fred = alloca i32
	%jim = alloca i32
	store i32 5, i32* %fred
	%14 = load i32, i32* %fred
	%15 = add nsw i32 7, %14
	store i32 %15, i32* %jim
	%16 = load i32, i32* %fred
	%17 = load i32, i32* %jim
	%18 = add nsw i32 %16, %17
	%19 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %18)
	%20 = icmp eq i32 7, 9
	%21 = zext i1 %20 to i32
	%22 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %21)
	%23 = icmp ne i32 7, 9
	%24 = zext i1 %23 to i32
	%25 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %24)
	%26 = icmp slt i32 7, 9
	%27 = zext i1 %26 to i32
	%28 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %27)
	%29 = icmp sgt i32 7, 9
	%30 = zext i1 %29 to i32
	%31 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %30)
	%32 = icmp sle i32 7, 9
	%33 = zext i1 %32 to i32
	%34 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %33)
	%35 = icmp sge i32 7, 9
	%36 = zext i1 %35 to i32
	%37 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %36)
	%fib1 = alloca i32
	store i32 0, i32* %fib1
	%fib2 = alloca i32
	store i32 1, i32* %fib2
	br label %.block.1
.block.1:
	%38 = load i32, i32* %fib2
	%39 = icmp sle i32 %38, 1000
	br i1 %39, label %.block.2, label %.block.3
.block.2:
	%40 = load i32, i32* %fib2
	%41 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %40)
	%fibTemp = alloca i32
	%42 = load i32, i32* %fib2
	store i32 %42, i32* %fibTemp
	%43 = load i32, i32* %fib1
	%44 = load i32, i32* %fib2
	%45 = add nsw i32 %43, %44
	store i32 %45, i32* %fib2
	%46 = load i32, i32* %fibTemp
	store i32 %46, i32* %fib1
	br label %.block.1
.block.3:
	%mystery = alloca i32
	store i32 10, i32* %mystery
	%47 = load i32, i32* %mystery
	%48 = icmp sgt i32 9, %47
	br i1 %48, label %.block.4, label %.block.5
.block.4:
	%49 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 9)
	br label %.block.6
.block.5:
	%50 = load i32, i32* %mystery
	%51 = icmp slt i32 11, %50
	br i1 %51, label %.block.7, label %.block.8
.block.7:
	%52 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 11)
	br label %.block.9
.block.8:
	%53 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 10)
	%54 = load i32, i32* %mystery
	%55 = icmp eq i32 %54, 10
	br i1 %55, label %.block.10, label %.block.11
.block.10:
	%56 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 1)
	br label %.block.11
.block.11:
	br label %.block.9
.block.9:
	br label %.block.6
.block.6:
	%57 = call i32 @square(i32 5)
	%58 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %57)
	%59 = call i32 @gcd(i32 20, i32 45)
	%60 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %59)
	ret i32 0
}

declare i32 @printf(i8*, ...)
