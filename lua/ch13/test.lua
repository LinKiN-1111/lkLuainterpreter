function innerFunction()
    print("运行了innerFunction函数!")
    error("内部出错啦！")  -- 抛出错误
    print("这行不会执行")
end

function middleFunction()
    innerFunction()  -- 没有 pcall，错误继续向上抛
    print("这行也不会执行")
end

function outerFunction()
    middleFunction()  -- 没有 pcall，错误继续向上抛
    print("这行也不会执行")
end

-- 最外层用 pcall 捕获错误
local status, err = pcall(outerFunction)
if not status then
    print("catch the error:", err)
end
print("yes!")  -- 这行会执行