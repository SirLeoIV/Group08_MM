x = [5, 10, 15, 20, 25, 30, 35, 40];
y = [0.22, 0.48, 0.72, 0.98, 1.23, 1.38, 1.31, 1.23];
p = polyfit(x,y,7);
x1 = [0, 5, 10, 15, 20, 25, 30, 35, 40];

% load data from the file Coefficients_Data via terminal: data = [...]
x2 = data(:,1)
y2 = data(:,3)
x1 = linspace(0, 45);
y1 = polyval(p,x1);
figure
%plot(x,y,'o')
hold on
%plot(x1,y1)
plot(x2, y2, 'o')
c10 = polyfit(x2, y2, 10)
%plot(x2, polyval(c10, x2))
c16 = polyfit(x2, y2, 16)
%plot(x2, polyval(c16, x2));
c18 = polyfit(x2, y2, 18);
%plot(x2, polyval(c18, x2));
c20 = polyfit(x2, y2, 20);
%plot(x2, polyval(c20, x2));
%legend('data', '10', '16', '18', '20')
%hold off;

f = @(x) -7.11145860926042e-19 * x.^10 ... 
    + 7.29835098673025e-16 * x.^9 ...
    - 3.17294516756617e-13 * x.^8 ...
    + 7.63300503284026e-11 * x.^7 ...
    - 1.11225338175080e-08 * x.^6 ...
    + 1.00853574891538e-06 * x.^5 ...
    - 5.61414835267720e-05 * x.^4 ...
    + 0.00181651020401031 * x.^3 ...
    - 0.0308244844086802 * x.^2 ...
    + 0.241539695258776 * x.^1 ...
    - 0.0455606772480888;

plot(x2, f(x2))

f2 = @(x) - 2.34883452212131e-20  * x.^10 ...
    + 1.16915339371553e-17  * x.^9 ...
    - 7.19105243730552e-16  * x.^8 ...
    - 6.78303608765851e-13  * x.^7 ...
    + 2.00518143371719e-10  * x.^6 ...
    - 2.61271931318120e-08  * x.^5 ...
    + 1.92548190311079e-06  * x.^4 ...
    - 9.01804243068557e-05  * x.^3 ...
    + 0.00275939330472374   * x.^2 ...
    - 0.0180052597286692    * x.^1 ...
    + 0.0230541325838875;

plot(x2, f2(x2))
