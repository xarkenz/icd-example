source_filename = "test_program.txt"
target triple = "x86_64-pc-linux-gnu"

@print_int_fstring = private unnamed_addr constant [4 x i8] c"%d\0A\00"

define i32 @main() {
	%1 = add nsw i32 10, 8
	%2 = sdiv i32 6, 6
	%3 = mul nsw i32 %2, 2
	%4 = sub nsw i32 %1, %3
	%5 = sdiv i32 8, 4
	%6 = add nsw i32 %4, %5
	%7 = sub nsw i32 %6, 1
	%8 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %7)
	%9 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 5)
	%10 = sub nsw i32 0, 5
	%11 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %10)
	%12 = sub nsw i32 5, 3
	%13 = add nsw i32 %12, 2
	%14 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %13)
	%fred = alloca i32
	%jim = alloca i32
	store i32 5, i32* %fred
	%15 = load i32, i32* %fred
	%16 = add nsw i32 7, %15
	store i32 %16, i32* %jim
	%17 = load i32, i32* %fred
	%18 = load i32, i32* %jim
	%19 = add nsw i32 %17, %18
	%20 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %19)
	%21 = icmp eq i32 7, 9
	%22 = zext i1 %21 to i32
	%23 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %22)
	%24 = icmp ne i32 7, 9
	%25 = zext i1 %24 to i32
	%26 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %25)
	%27 = icmp slt i32 7, 9
	%28 = zext i1 %27 to i32
	%29 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %28)
	%30 = icmp sgt i32 7, 9
	%31 = zext i1 %30 to i32
	%32 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %31)
	%33 = icmp sle i32 7, 9
	%34 = zext i1 %33 to i32
	%35 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %34)
	%36 = icmp sge i32 7, 9
	%37 = zext i1 %36 to i32
	%38 = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %37)
	ret i32 0
}

declare i32 @printf(i8*, ...)
