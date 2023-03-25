arr = [6, 5, 3, 2, 2, 1, 0]
x = 3

lo, hi = 0, len(arr) - 1
result = -1

while lo <= hi:
    mid = (lo + hi) // 2
    if arr[mid] <= x:
        result = mid
        hi = mid - 1
        if arr[hi] > x:
            break
    else:
        lo = mid + 1

print(result)
