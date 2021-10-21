# Spring BootによるREST API構築

## git emoji prefixの設定

Windows環境ではgitbash等のbashを実行できる環境を用意するかコマンドを変更してください
```
git config core.hooksPath .githooks
chmod a+x .githooks/prepare-commit-msg
```

## データベースの構築方法
```shell
docker-compose up -d
```

