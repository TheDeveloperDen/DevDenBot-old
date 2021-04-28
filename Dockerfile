FROM fsharp
FROM mcr.microsoft.com/dotnet/sdk:5.0
WORKDIR /app
COPY . /app
VOLUME /var/data
CMD dotnet run --project DevDenBot.fsproj