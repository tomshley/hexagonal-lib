import sbt.*

object Registry {
  def credentials(credentialFileOption: Option[File]): Credentials =
    sys.env
      .get("CI_JOB_TOKEN")
      .map(
        Credentials(
          "GitLab Packages Registry",
          "gitlab.com",
          "gitlab-ci-token",
          _
        )
      )
      .getOrElse(Credentials(credentialFileOption.getOrElse(file(".credentials.gitlab"))))

  def additionalResolvers(projectId: Int): Seq[MavenRepository] = Seq(
    "GitLab" at
      s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
  )

  def publishToGitlab(projectId: Int): Option[MavenRepository] =
    Some(
      "gitlab" at s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
    )
}
