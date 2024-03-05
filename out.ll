source_filename = "test_program.txt"
target triple = "x86_64-pc-linux-gnu"

@print_int_fstring = private unnamed_addr constant [4 x i8] c"%d\0A\00"

define i32 @main() {
	%1 = alloca i32
	store i32 10, i32* %1
	%2 = load i32, i32* %1
	%3 = alloca i32
	store i32 8, i32* %3
	%4 = load i32, i32* %3
	%5 = add nsw i32 %2, %4
	%6 = alloca i32
	store i32 6, i32* %6
	%7 = load i32, i32* %6
	%8 = alloca i32
	store i32 6, i32* %8
	%9 = load i32, i32* %8
	%10 = sdiv i32 %7, %9
	%11 = alloca i32
	store i32 2, i32* %11
	%12 = load i32, i32* %11
	%13 = mul nsw i32 %10, %12
	%14 = sub nsw i32 %5, %13
	%15 = alloca i32
	store i32 8, i32* %15
	%16 = load i32, i32* %15
	%17 = alloca i32
	store i32 4, i32* %17
	%18 = load i32, i32* %17
	%19 = sdiv i32 %16, %18
	%20 = add nsw i32 %14, %19
	%21 = alloca i32
	store i32 1, i32* %21
	%22 = load i32, i32* %21
	%23 = sub nsw i32 %20, %22
	call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 %23)
	ret i32 0
}

declare i32 @printf(i8*, ...)
